package fr.high4technology.high4resto.bean.Serveur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

import fr.high4technology.high4resto.Util.Util;
import fr.high4technology.high4resto.WebSocket.BarWaiterCanalHandler;
import fr.high4technology.high4resto.WebSocket.ColdCookCanalHandler;
import fr.high4technology.high4resto.WebSocket.CookCanalHandler;
import fr.high4technology.high4resto.WebSocket.HotCookCanalHandler;
import fr.high4technology.high4resto.WebSocket.WineStewardCanalHandler;
import fr.high4technology.high4resto.bean.Audio.Audio;
import fr.high4technology.high4resto.bean.Audio.AudioRepository;
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
import fr.high4technology.high4resto.bean.commande.Commande;
import fr.high4technology.high4resto.bean.commande.CommandeRepository;
import fr.high4technology.high4resto.bean.table.Table;
import fr.high4technology.high4resto.bean.table.TableRepository;

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
    VoiceSelectionParams voice = VoiceSelectionParams.newBuilder().setLanguageCode("fr-FR")
    .setSsmlGender(SsmlVoiceGender.MALE).build();

    @Autowired
    private CommandeRepository commandes;
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
    @Autowired
    private AudioRepository audios;
    @Autowired
    private TableRepository tables;

    private final ReactiveGridFsTemplate gridFsTemplate;

    private void sendToCanal(String canal, String message) {
        switch (canal) {
            case "ROLE_WINESTEWARD":
                wineStewardCanal.sendMessage(message);
                break;
            case "ROLE_BARWAITER":
                barWaiterCanal.sendMessage(message);
                break;
            case "ROLE_HOTCOOK":
                hotCookCanal.sendMessage(message);
                break;
            case "ROLE_COLDCOOK":
                coldCookCanal.sendMessage(message);
                break;
            case "ROLE_COOK":
                cookCanal.sendMessage(message);
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

    @GetMapping("/createCommande/{table}/{mandatory}")
    public Mono<Commande> generateCommande(@PathVariable String table,@PathVariable String mandatory)
    {
        return this.commandes.count()
        .flatMap(result->{
            return commandes.save(Commande.builder().finish(false).number(result).inside(Util.getTimeNow()).destination(table).deleveryMode("inside").mandatory(mandatory).build());
        });
    }

    @PutMapping("/updateCommande/")
    public Mono<Commande> updateCommande(@RequestBody Commande commande)
    {
        return this.commandes.findById(commande.getId()).map(found->{
            found.setClient(commande.getClient());
            found.setFinish(commande.getFinish());
            found.setItems(commande.getItems());
            found.setMessage(commande.getMessage());
            found.setStatus(commande.getStatus());
            return found;
        }).flatMap(commandes::save);
    }

    @GetMapping("/findCommande/{table}/{mandatory}")
    public Flux<Commande> findCommande(@PathVariable String table,@PathVariable String mandatory)
    {
        return (this.commandes.findAll()
        .filter(a->!a.getFinish())
        .filter(a->a.getDestination().equals(table)))

        .switchIfEmpty(this.generateCommande(table, mandatory).flatMapMany(result->{
            return Flux.just(result);
        }));
    }

    @GetMapping("/findTable/")
    public Flux<Table> getAllAll() {
        return tables.findAll();
    }

    @GetMapping("/findTable/{idItem}")
    public Mono<Table> getById(@PathVariable String idItem) {
        return tables.findById(idItem);
    }

    @DeleteMapping("/deleteTable/{idItem}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String idItem) {

        return tables.deleteById(idItem).map(r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.ok().<Void>build());
    }

    @PutMapping("/insertTable/")
    Mono<Table> insert(@RequestBody Table table) {
        return tables.save(table);
    }

    @PutMapping("/updateTable/")
    Mono<Table> update(@RequestBody Table table) {
        return tables.findById(table.getId()).map(foundItem -> {
            foundItem.setName(table.getName());
            foundItem.setPlace(table.getPlace());
            return foundItem;
        }).flatMap(tables::save);
    }

    @PutMapping("/moveToPreorder/{idCommande}")
    public Mono<PreOrder> moveToPreorder(@RequestBody PreOrder preOrder,@RequestParam String idCommande) {
        preOrder.setInside(Util.getTimeNow());
        preOrder.setId(preOrder.getStock().getId());
        preOrder.getStock().getItem().setStock(1);
        return this.stocks.deleteById(preOrder.getStock().getId())
        .then(
            this.commandes.findById(idCommande)
            .map(result->{
                if(result.getItems().size()<1)
                result.setInside(Util.getTimeNow());
                result.getItems().add(preOrder);
                return result;
            })
            .flatMap(commandes::save)
        )
        .then(this.preOrders.save(preOrder));
    }

    @GetMapping("/moveManyToOrder/{table}/{mandatory}")
    public Mono<Order> moveManyToOrder(@PathVariable String table,@PathVariable String mandatory)
    {
        return this.preOrders.findAll().filter(preorder->preorder.getDestination().equals(table))
        .sort((a,b)->{
            int ida=a.getStock().getItem().getCategorie().getOrder();
            int idb=b.getStock().getItem().getCategorie().getOrder();
            if(ida>idb)
                return 1;
            if(ida<idb)
                return -1;
            else
                return 0;
        }).flatMap(preorder->{
            return Mono.just(Order.builder()
            .deliveryMode("inside")
            .mandatory(mandatory)
            .toTake(false)
            .preOrder(preorder).build());
        })
        .delayElements(Duration.ofSeconds(8)).flatMap(this::moveToOrder)
        .then(Mono.just(Order.builder().build()));
    }

    @GetMapping("/moveManyToTake/{table}/{idCategory}")
    public Mono<Order> moveManyToTake(@PathVariable String table,@PathVariable String idCategory)
    {
        return this.orders.findAll()
        .filter(order->!order.isToTake())
        .filter(order->order.getPreOrder().getDestination().equals(table))
        .filter(order->order.getPreOrder().getStock().getItem().getCategorie().getId().equals(idCategory))
        .sort((a,b)->{
            int ida=a.getPreOrder().getStock().getItem().getCategorie().getOrder();
            int idb=b.getPreOrder().getStock().getItem().getCategorie().getOrder();
            if(ida>idb)
                return 1;
            if(ida<idb)
                return -1;
            else
                return 0;
        })
        .delayElements(Duration.ofSeconds(8)).flatMap(this::moveToTake)
        .then(Mono.just(Order.builder().build()));
    }

    @PutMapping("/moveToOrder/")
    public Mono<Order> moveToOrder(@RequestBody Order order) {
        Queue<String> role = new ConcurrentLinkedQueue<String>();
        // Je définis l'heure et redéfinis le stock à 1
        order.setInside(Util.getTimeNow());
        order.getPreOrder().getStock().getItem().setStock(1);
        order.setId(order.getPreOrder().getId());
        // Je génère le texte d'annonce
        StringBuilder text=new StringBuilder();
        text.append("J'annonce pour la table "+order.getPreOrder().getDestination()+".");
        text.append(order.getPreOrder().getStock().getItem().getName()+"!");
        text.append(Util.generateTextForSpeach(order.getPreOrder().getStock().getItem()));
        text.append(order.getPreOrder().getMessageToNext());
        order.setAnnonce(text.toString().substring(text.toString().indexOf('!')+1));

        // Je supprime preorder
        return this.preOrders.deleteById(order.getPreOrder().getId())
        // Je cherche qui prend en charge cet item et j'enregistre dans rôle
        .then
        (
            this.itemPreparations.findById(order.getPreOrder().getStock().getItem().getId())
            .flatMap(result->{
                role.addAll(result.getRoleName());
                return Mono.empty();
            })
        )
        // Je cherche si l'audio existe dans la base de donnée
        .then
        (
            this.audios.findById(Util.hash(text.toString()))
            .flatMap(audio->{
                log.warn("Audio existe je ne le génère pas");
                order.setIdAnnonce(audio.getGridId());
                role.forEach(roles->{
                    this.sendToCanal(roles, "audio:"+audio.getGridId());
                    this.sendToCanal(roles,"update:"+"annonce");
                });
                return this.orders.save(order);
            })
        )
        // si il n'y est pas je le génère
        .switchIfEmpty(
                Mono.just("")
                .flatMap(i->{
                    log.warn("Audio n'existe pas je le génère");
                    String fileName=Util.randomIdentifier()+".mp3";
                    try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create())
                    {
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
                    }).flatMap(id->{
                        order.setIdAnnonce(id.toHexString());
                        role.forEach(roles->{
                            this.sendToCanal(roles, "audio:"+id.toHexString());
                            this.sendToCanal(roles,"update:"+"annonce");
                        });
                        return audios.save(Audio.builder().gridId(id.toHexString()).id(Util.hash(text.toString())).build());
                    })
                .then
                (
                    this.orders.save(order)
                )
                );
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
    public Mono<Order> moveToTake(@RequestBody Order order)
    {

        Queue<String> role = new ConcurrentLinkedQueue<String>();
        // Je définis l'heure et redéfinis le stock à 1 et to Take a true
        order.setInside(Util.getTimeNow());
        order.getPreOrder().getStock().getItem().setStock(1);
        order.setToTake(true);
        order.setId(order.getPreOrder().getId());
        // Je génère le texte d'annonce
        StringBuilder text=new StringBuilder();
        text.append("Je demande l'envoie pour la table "+order.getPreOrder().getDestination()+".");
        text.append(order.getPreOrder().getStock().getItem().getName()+"!");
        text.append(Util.generateTextForSpeach(order.getPreOrder().getStock().getItem()));
        text.append(order.getPreOrder().getMessageToNext());

        // Je supprime preorder
        return this.preOrders.deleteById(order.getPreOrder().getId())
        // Je cherche qui prend en charge cet item et j'enregistre dans rôle
        .then
        (
            this.itemPreparations.findById(order.getPreOrder().getStock().getItem().getId())
            .flatMap(result->{
                role.addAll(result.getRoleName());
                return Mono.empty();
            })
        )
        // Je cherche si l'audio existe dans la base de donnée
        .then
        (
            this.audios.findById(Util.hash(text.toString()))
            .flatMap(audio->{
                order.setAnnonce(text.toString().substring(text.toString().indexOf('!')+1));
                log.warn("Audio existe je ne le génère pas");
                order.setIdAnnonce(audio.getGridId());
                role.forEach(roles->{
                    this.sendToCanal(roles, "audio:"+audio.getGridId());
                    this.sendToCanal(roles,"update:"+"annonce");
                });
                return this.orders.save(order);
            })
        )
        // si il n'y est pas je le génère
        .switchIfEmpty(
                Mono.just("")
                .flatMap(i->{
                    log.warn("Audio n'existe pas je le génère");
                    String fileName=Util.randomIdentifier()+".mp3";
                    try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create())
                    {
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
                    }).flatMap(id->{
                        order.setIdAnnonce(id.toHexString());
                        role.forEach(roles->{
                            this.sendToCanal(roles, "audio:"+id.toHexString());
                            this.sendToCanal(roles,"update:"+"annonce");
                        });
                        return audios.save(Audio.builder().gridId(id.toHexString()).id(Util.hash(text.toString())).build());
                    })
                .then
                (
                    this.orders.save(order)
                )
                );
    }

    @PutMapping("/moveBackToStock/{idCommande}")
    public Mono<Stock> moveBackToStock(@RequestBody PreOrder preOrder,@RequestParam String idCommande) {
        preOrder.getStock().getItem().setStock(1);
        return this.preOrders.deleteById(preOrder.getId())
        .then(
            this.commandes.findById(idCommande)
            .map(result->{
                result.getItems().removeIf(a->a.getId().equals(preOrder.getId()));
                return result;
            })
            .flatMap(this.commandes::save)
        )
        .then(stocks.save(preOrder.getStock()));
    }



}
