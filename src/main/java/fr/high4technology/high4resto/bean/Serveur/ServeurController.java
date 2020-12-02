package fr.high4technology.high4resto.bean.Serveur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
import org.springframework.http.CacheControl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import fr.high4technology.high4resto.Util.Util;
import fr.high4technology.high4resto.WebSocket.BarWaiterCanalHandler;
import fr.high4technology.high4resto.WebSocket.ColdCookCanalHandler;
import fr.high4technology.high4resto.WebSocket.CookCanalHandler;
import fr.high4technology.high4resto.WebSocket.HotCookCanalHandler;
import fr.high4technology.high4resto.WebSocket.WineStewardCanalHandler;
import fr.high4technology.high4resto.bean.ItemCarte.ItemCarte;
import fr.high4technology.high4resto.bean.ItemCategorie.ItemCategorie;
import fr.high4technology.high4resto.bean.ItemCategorie.ItemCategorieRepository;

import fr.high4technology.high4resto.bean.ItemPreparation.ItemPreparationRepository;
import fr.high4technology.high4resto.bean.Stock.Stock;
import fr.high4technology.high4resto.bean.Stock.StockRepository;
import fr.high4technology.high4resto.bean.Tracability.Order.Order;
import fr.high4technology.high4resto.bean.Tracability.Order.OrderRepository;
import fr.high4technology.high4resto.bean.Tracability.PreOrder.PreOrder;
import fr.high4technology.high4resto.bean.Tracability.PreOrder.PreOrderRepository;
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@RestController
@RequestMapping("/api/serveur")
@RequiredArgsConstructor
@Slf4j
public class ServeurController {
    VoiceSelectionParams voice = VoiceSelectionParams.newBuilder().setName("fr-FR-Wavenet-B").setLanguageCode("fr-FR")
    .setSsmlGender(SsmlVoiceGender.MALE).build();
    @Autowired
    private StockRepository stocks;
    @Autowired
    private ItemPreparationRepository itemPreparations;
    @Autowired
    private ItemCategorieRepository itemCategories;
    @Autowired
    private PreOrderRepository preOrders;
    @Autowired
    private OrderRepository orders;
    @Autowired
    private WineStewardCanalHandler wineStewardCanal;
    @Autowired
    private BarWaiterCanalHandler barWaiterCanal;
    @Autowired
    private HotCookCanalHandler hotCookCanal;
    @Autowired
    private ColdCookCanalHandler coldCookCanal;
    @Autowired
    private CookCanalHandler cookCanal;

    private final ReactiveGridFsTemplate gridFsTemplate;

    private void sendToCanal(String canal, String message) {
        switch (canal) {
            case "ROLE_WINESTEWARD":
                this.wineStewardCanal.sendMessage(message);
                break;
            case "ROLE_BARWAITER":
                this.barWaiterCanal.sendMessage(message);
                break;
            case "ROLE_HOTCOOK":
                this.hotCookCanal.sendMessage(message);
                break;
            case "ROLE_COLDCOOK":
                this.coldCookCanal.sendMessage(message);
                break;
            case "ROLE_COOK":
                this.cookCanal.sendMessage(message);
                break;
        }
    }

    @GetMapping("/findCategory/")
    public Flux<ItemCategorie> getAll() {
        return itemCategories.findAll();
    }

    @GetMapping("/findOrder/{table}")
    public Flux<Order> findOrder(@PathVariable String table) {
        return this.orders.findAll().filter(order -> {
            return (order.getPreOrder().getDestination().equals(table)) && (!order.isToTake());
        });
    }

    @GetMapping("/findPreOrders/{table}")
    public Flux<PreOrder> findPreOrders(@PathVariable String table) {
        return this.preOrders.findAll().filter(preOrder -> {
            return preOrder.getDestination().equals(table);
        });
    }

    @GetMapping("/findStocks/{idCategorie}")
    public Flux<Stock> getGrouped(@PathVariable String idCategorie) {
        return stocks.findAll().filter(stock -> stock.getItem().getCategorie().getId().equals(idCategorie))
                .sort((a, b) -> {
                    return a.getItem().getId().compareTo(b.getItem().getId());
                })
                // Je regroupe le tout et je compte le stock disponible
                .transformDeferred(source -> {
                    AtomicReference<Stock> last = new AtomicReference<>(null);
                    Stock stock = Stock.builder().item(ItemCarte.builder().stock(0).build()).build();
                    last.set(stock);
                    return source
                            .windowUntil(i -> !i.getItem().getId().equals(last.getAndSet(i).getItem().getId()), true)
                            .flatMap(window -> window.reduce((i1, i2) -> {
                                i1.getItem().setStock(i1.getItem().getStock() + i2.getItem().getStock());
                                return i1;
                            }));
                });
    }

    @PutMapping("/moveToPreorder/")
    Mono<PreOrder> moveToPreorder(@RequestBody PreOrder preOrder) {
        preOrder.setInside(Util.getTimeNow());
        preOrder.getStock().getItem().setStock(1);
        return this.stocks.deleteById(preOrder.getStock().getId()).then(this.preOrders.save(preOrder));
    }

    @PutMapping("/moveToOrder/")
    Mono<Order> moveToOrder(@RequestBody Order order) {
        order.setInside(Util.getTimeNow());
        order.getPreOrder().getStock().getItem().setStock(1);
        final List<String> role = new ArrayList<String>();

        return this.preOrders.deleteById(order.getPreOrder().getId()).then(this.orders.save(order)).then(
                this.itemPreparations.findById(order.getPreOrder().getStock().getItem().getId()).flatMap(result -> {
                    role.addAll(result.getRoleName());
                    String fileName=Util.randomIdentifier()+".mp3";
                    try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create())

                    {
                        StringBuilder text=new StringBuilder();
                        text.append("J'annonce pour la table "+order.getPreOrder().getDestination()+".");
                        text.append(order.getPreOrder().getStock().getItem().getName());
                        order.getPreOrder().getStock().getItem().getOptions().forEach(option->{
                            option.getOptions().forEach(choix->{
                                if(choix.isSelected())
                                {
                                    text.append(" avec comme choix pour ");
                                    text.append(option.getLabel());
                                    text.append(" "+choix.getLabel());
                                }
                            });
                        });
                        text.append("."+order.getPreOrder().getMessageToNext());
                        SynthesisInput input = SynthesisInput.newBuilder().setText(text.toString()).build();

                        AudioConfig audioConfig = AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.MP3).build();

                        SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice,
                                audioConfig);

                        ByteString audioContents = response.getAudioContent();
                        DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
                        DefaultDataBuffer finalFile;
                        finalFile = factory.wrap(audioContents.toByteArray());

                        return this.gridFsTemplate.store(Flux.just(finalFile), fileName);
                    }
                    catch(Exception e)
                    {
                        log.warn(e.getMessage());
                    }

                    return this.gridFsTemplate.store(Flux.just(), fileName);
                })).flatMap(id->{
                    role.forEach(roles->{
                        this.sendToCanal(roles, id.toHexString());
                    });
                    return Mono.empty();
                }).then(Mono.just(order));
    }

	@GetMapping("/download/{id}")
	public Flux<Void> read(@PathVariable String id, ServerWebExchange exchange) {
		return this.gridFsTemplate.findOne(query(where("_id").is(id))).flatMap(gridFsTemplate::getResource)
				.flatMapMany(r -> {
					exchange.getResponse().getHeaders()
							.setCacheControl(CacheControl.maxAge(Duration.ofSeconds(3600)).cachePrivate());
					return exchange.getResponse().writeWith(r.getDownloadStream());
				});
	}

    @PutMapping("/moveToTake/")
    Mono<Order> moveToTake(@RequestBody Order order)
    {
        final List<String> role = new ArrayList<String>();
        order.setToTake(true);
        return this.orders.save(order).then(
            this.itemPreparations.findById(order.getPreOrder().getStock().getItem().getId()).flatMap(result -> {
                role.addAll(result.getRoleName());
                String fileName=Util.randomIdentifier()+".mp3";
                try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create())

                {
                    StringBuilder text=new StringBuilder();
                    text.append("Je demande l'envoie pour la table "+order.getPreOrder().getDestination()+"!");
                    text.append(order.getPreOrder().getStock().getItem().getName());
                    order.getPreOrder().getStock().getItem().getOptions().forEach(option->{
                        option.getOptions().forEach(choix->{
                            if(choix.isSelected())
                            {
                                text.append(" avec comme choix pour ");
                                text.append(option.getLabel());
                                text.append(" "+choix.getLabel());
                            }
                        });
                    });
                    text.append("."+order.getPreOrder().getMessageToNext());
                    SynthesisInput input = SynthesisInput.newBuilder().setText(text.toString()).build();

                    AudioConfig audioConfig = AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.MP3).build();

                    SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice,
                            audioConfig);

                    ByteString audioContents = response.getAudioContent();
                    DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
                    DefaultDataBuffer finalFile;
                    finalFile = factory.wrap(audioContents.toByteArray());

                    return this.gridFsTemplate.store(Flux.just(finalFile), fileName);
                }
                catch(Exception e)
                {
                    log.warn(e.getMessage());
                }

                return this.gridFsTemplate.store(Flux.just(), fileName);
            })).flatMap(id->{
                role.forEach(roles->{
                    this.sendToCanal(roles, id.toHexString());
                });
                return Mono.empty();
            }).then(Mono.just(order));
    }

    @PutMapping("/moveBackToStock/")
    Mono<Stock> moveBackToStock(@RequestBody PreOrder preOrder) {
        preOrder.getStock().getItem().setStock(1);
        return this.preOrders.deleteById(preOrder.getId()).then(stocks.save(preOrder.getStock()));
    }



}
