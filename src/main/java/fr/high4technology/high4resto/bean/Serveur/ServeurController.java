package fr.high4technology.high4resto.bean.Serveur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

import fr.high4technology.high4resto.Util.Util;
import fr.high4technology.high4resto.WebSocket.BarWaiterCanalHandler;
import fr.high4technology.high4resto.WebSocket.ColdCookCanalHandler;
import fr.high4technology.high4resto.WebSocket.CookCanalHandler;
import fr.high4technology.high4resto.WebSocket.HotCookCanalHandler;
import fr.high4technology.high4resto.WebSocket.WineStewardCanalHandler;
import fr.high4technology.high4resto.bean.Concurrency;
import fr.high4technology.high4resto.bean.ItemCarte.ItemCarte;
import fr.high4technology.high4resto.bean.ItemCategorie.ItemCategorie;
import fr.high4technology.high4resto.bean.ItemCategorie.ItemCategorieRepository;

import fr.high4technology.high4resto.bean.OptionItem.OptionItem;
import fr.high4technology.high4resto.bean.OptionItem.OptionsItem;
import fr.high4technology.high4resto.bean.Stock.Stock;
import fr.high4technology.high4resto.bean.Stock.StockRepository;
import fr.high4technology.high4resto.bean.Struct.Annonce;
import fr.high4technology.high4resto.bean.Tracability.Delevery.Delevery;
import fr.high4technology.high4resto.bean.Tracability.Delevery.DeleveryRepository;
import fr.high4technology.high4resto.bean.Tracability.Histrory.History;
import fr.high4technology.high4resto.bean.Tracability.Histrory.HistoryRepository;
import fr.high4technology.high4resto.bean.Tracability.Order.Order;
import fr.high4technology.high4resto.bean.Tracability.Order.OrderRepository;
import fr.high4technology.high4resto.bean.Tracability.PreOrder.PreOrder;
import fr.high4technology.high4resto.bean.Tracability.PreOrder.PreOrderRepository;
import fr.high4technology.high4resto.bean.Tracability.Prepare.Prepare;
import fr.high4technology.high4resto.bean.Tracability.Prepare.PrepareRepository;
import fr.high4technology.high4resto.bean.Tracability.ToDelivery.ToDelivery;
import fr.high4technology.high4resto.bean.Tracability.ToDelivery.ToDeliveryRepository;
import fr.high4technology.high4resto.bean.Tracability.Trash.Trash;
import fr.high4technology.high4resto.bean.Tracability.Trash.TrashRepository;
import fr.high4technology.high4resto.bean.commande.Commande;
import fr.high4technology.high4resto.bean.commande.CommandeRepository;
import fr.high4technology.high4resto.bean.table.Table;
import fr.high4technology.high4resto.bean.table.TableRepository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import fr.high4technology.high4resto.bean.Struct.ElementAnnonce;

@RestController
@RequestMapping("/api/serveur")
@RequiredArgsConstructor
public class ServeurController {

    @Autowired
    private CommandeRepository commandes;
    @Autowired
    private StockRepository stocks;
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
    private TableRepository tables;
    @Autowired
    private PrepareRepository prepares;
    @Autowired
    private ToDeliveryRepository toDeliverys;
    @Autowired
    private DeleveryRepository deleverys;
    @Autowired
    private HistoryRepository historys;
    @Autowired
    private TrashRepository trashs;

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

    @GetMapping("/findPrepared/{mandatory}")
    public Flux<Prepare> findPrepared(@PathVariable String mandatory) {
        return this.prepares.findAll().filter(a -> a.getToPrepare().getOrder().getMandatory().equals(mandatory));
    }

    @GetMapping("/findToDelevery/{mandatory}")
    public Flux<ToDelivery> findToDelevery(@PathVariable String mandatory) {
        return this.toDeliverys.findAll()
                .filter(a -> a.getPrepare().getToPrepare().getOrder().getMandatory().equals(mandatory));
    }

    @GetMapping("/findDelevery/{mandatory}")
    public Flux<Delevery> findDelevery(@PathVariable String mandatory) {
        return this.deleverys.findAll()
                .filter(a -> a.getToDelivery().getPrepare().getToPrepare().getOrder().getMandatory().equals(mandatory));
    }

    @GetMapping("/findCategory/")
    public Flux<ItemCategorie> getAll() {
        return itemCategories.findAll();
    }

    @GetMapping("/findOrder/{table}")
    public Flux<Order> findOrder(@PathVariable String table) {
        return this.orders.findAll().filter(order -> {
            return (order.getPreOrder().getDestination().equals(table)) && (!order.isToTake());
        }).sort((a, b) -> {
            if (a.getPreOrder().getStock().getItem().getCategorie().getOrder() > b.getPreOrder().getStock().getItem()
                    .getCategorie().getOrder())
                return 1;
            else if (b.getPreOrder().getStock().getItem().getCategorie().getOrder() > a.getPreOrder().getStock()
                    .getItem().getCategorie().getOrder())
                return -1;
            else
                return 0;

        });
    }

    @GetMapping("/findPreOrders/{table}")
    public Flux<PreOrder> findPreOrders(@PathVariable String table) {
        return this.preOrders.findAll().filter(preOrder -> {
            return preOrder.getDestination().equals(table);
        }).sort((a, b) -> {
            if (a.getStock().getItem().getCategorie().getOrder() > b.getStock().getItem().getCategorie().getOrder())
                return 1;
            else if (b.getStock().getItem().getCategorie().getOrder() > a.getStock().getItem().getCategorie()
                    .getOrder())
                return -1;
            else
                return 0;

        });
    }

    @GetMapping("/findCommande/{table}/{mandatory}")
    public Flux<Commande> findCommande(@PathVariable String table, @PathVariable String mandatory) {
        return (this.commandes.findAll().filter(a -> !a.getFinish()).filter(a -> a.getDestination().equals(table)))

                .switchIfEmpty(this.generateCommande(table, mandatory).flatMapMany(result -> {
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
    public Mono<Commande> generateCommande(@PathVariable String table, @PathVariable String mandatory) {
        return this.commandes.count().flatMap(result -> {
            return commandes.save(Commande.builder().finish(false).number(result).inside(Util.getTimeNow())
                    .destination(table).deleveryMode("inside").mandatory(mandatory).build());
        });
    }

    @PutMapping("/insertTable/")
    Mono<Table> insert(@RequestBody Table table) {
        return tables.save(table);
    }

    @PutMapping("/updateCommande/")
    public Mono<Commande> updateCommande(@RequestBody Commande commande) {
        return this.commandes.findById(commande.getId()).map(found -> {
            found.setClient(commande.getClient());
            found.setFinish(commande.getFinish());
            found.setItems(commande.getItems());
            found.setMessage(commande.getMessage());
            found.setStatus(commande.getStatus());
            return found;
        }).flatMap(commandes::save);
    }

    @PutMapping("/updateTable/")
    Mono<Table> update(@RequestBody Table table) {
        return tables.findById(table.getId()).map(foundItem -> {
            foundItem.setName(table.getName());
            foundItem.setPlace(table.getPlace());
            return foundItem;
        }).flatMap(tables::save);
    }

    @DeleteMapping("/deleteTable/{idItem}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String idItem) {

        return tables.deleteById(idItem).map(r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.ok().<Void>build());
    }

    @PutMapping("/moveBackToStock/")
    public Mono<Stock> moveBackToStock(@RequestBody PreOrder preOrder) {
        preOrder.getStock().getItem().setStock(1);
        return this.preOrders.deleteById(preOrder.getId())
                .then(this.commandes.findById(preOrder.getOrderNumber()).map(result -> {
                    result.getItems().removeIf(a -> a.getId().equals(preOrder.getId()));
                    return result;
                }).flatMap(this.commandes::save)).then(stocks.save(preOrder.getStock()));
    }

    @PutMapping("/moveToPreorder/")
    public Mono<PreOrder> moveToPreorder(@RequestBody PreOrder preOrder) {

        return this.stocks.findAll().filter(a -> a.getItem().getName().equals(preOrder.getStock().getItem().getName()))
                .collectList().flatMap(result -> {

                    for (Stock stock : result) {
                        if (!Concurrency.mapStock.containsKey(stock.getId())) {
                            Concurrency.mapStock.put(stock.getId(), 1);
                            preOrder.setId(stock.getId());
                            preOrder.setInside(Util.getTimeNow());
                            preOrder.getStock().getItem().setStock(1);
                            preOrder.setStock(stock);
                            preOrder.getStock().setItem(preOrder.getStock().getItem());
                            return this.stocks.deleteById(stock.getId());
                        }
                    }
                    return Mono.empty();
                }).then(this.commandes.findById(preOrder.getOrderNumber()).map(result -> {
                    if (result.getItems().size() < 1)
                        result.setInside(Util.getTimeNow());
                    result.getItems().add(preOrder);
                    return result;
                }).flatMap(commandes::save)).then(this.preOrders.save(preOrder));
    }

    @PutMapping("/moveToOrder/")
    public Mono<Annonce> moveToOrder2(@RequestBody Annonce annonce) {
        Queue<String> role = new ConcurrentLinkedQueue<String>();
        return Mono.just(annonce.getElements()).flatMapMany(elements -> {
            List<Order> orders = new ArrayList<Order>();
            for (ElementAnnonce element : elements) {
                orders.addAll(element.getOrders());
            }
            return Flux.fromIterable(orders);
        }).map(order -> {
            StringBuilder anonce = new StringBuilder();
            for (OptionsItem option : order.getPreOrder().getStock().getItem().getOptions()) {
                for (OptionItem choix : option.getOptions()) {
                    if (choix.isSelected())
                        anonce.append(choix.getLabel() + " ");
                }
            }
            anonce.append("." + order.getPreOrder().getMessageToNext());
            order.setAnnonce(anonce.toString());
            return order;
        }).flatMap(this::moveToOrder).flatMap(result -> {
            for (String ro : result.getPreOrder().getStock().getItem().getRoles()) {
                if (!role.contains(ro)) {
                    role.add(ro);

                }
            }

            return Mono.empty();
        }).then(Mono.fromRunnable(() -> {
            role.forEach(roles -> {
                this.sendToCanal(roles, "update:" + "annonce");
            });
        })).then(Mono.just(annonce));
    }

    private Mono<Order> moveToOrder(@RequestBody Order order) {
        // Je définis l'heure et redéfinis le stock à 1
        order.getPreOrder().getStock().getItem().setStock(1);
        order.setId(order.getPreOrder().getId());
        return this.preOrders.deleteById(order.getPreOrder().getId()).then(this.orders.save(order));
    }

    @PutMapping("/moveToTake/")
    public Mono<Annonce> moveToTake2(@RequestBody Annonce annonce) {
        Queue<String> role = new ConcurrentLinkedQueue<String>();
        return Mono.just(annonce.getElements()).flatMapMany(elements -> {
            List<Order> orders = new ArrayList<Order>();
            for (ElementAnnonce element : elements) {
                orders.addAll(element.getOrders());
            }
            return Flux.fromIterable(orders);
        }).flatMap(this::moveToTake).flatMap(result -> {
            for (String ro : result.getPreOrder().getStock().getItem().getRoles()) {
                if (!role.contains(ro)) {
                    role.add(ro);
                }
            }
            return Mono.empty();
        }).then(Mono.fromRunnable(() -> {
            role.forEach(roles -> {
                this.sendToCanal(roles, "update:" + "envoie");
            });
        })).then(Mono.just(annonce));
    }

    private Mono<Order> moveToTake(@RequestBody Order order) {
        order.setToTake(true);
        order.setInside(Util.getTimeNow());
        return this.orders.save(order);
    }

    @PutMapping("/moveToToDelevery/")
    public Mono<ToDelivery> moveToToDelevery(@RequestBody ToDelivery toDelivery) {
        toDelivery.setInside(Util.getTimeNow());
        toDelivery.setId(toDelivery.getPrepare().getId());
        return this.prepares.deleteById(toDelivery.getId()).then(this.toDeliverys.save(toDelivery));
    }

    @PutMapping("/moveToDelevery/")
    public Mono<Delevery> moveToDelivery(@RequestBody Delevery delevery) {
        delevery.setId(delevery.getToDelivery().getId());
        delevery.setInside(Util.getTimeNow());
        return this.toDeliverys.deleteById(delevery.getId()).then(this.deleverys.save(delevery));
    }

    @PutMapping("/moveToHistory/")
    public Mono<History> moveToHistory(@RequestBody History history) {
        history.setInside(Util.getTimeNow());
        history.setId(history.getDelevery().getId());
        return this.deleverys.deleteById(history.getId()).then(this.historys.save(history));
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

    @PutMapping("/movePreparedToTrash/")
    public Mono<Trash> movePreparedToTrash(@RequestBody Prepare prepare) {
        return this.prepares.deleteById(prepare.getId())
                .then(this.trashs.save(Trash.builder().id(prepare.getId())
                        .delevery(Delevery.builder().id(prepare.getId())
                                .toDelivery(ToDelivery.builder().id(prepare.getId()).prepare(prepare).build()).build())
                        .build()));
    }

    @PutMapping("/moveToDeleveryToTrash/")
    public Mono<Trash> moveToDeleveryToTrash(@RequestBody ToDelivery toDelivery) {
        return this.toDeliverys.deleteById(toDelivery.getId())
                .then(this.trashs.save(Trash.builder().id(toDelivery.getId())
                        .delevery(Delevery.builder().toDelivery(toDelivery).id(toDelivery.getId()).build()).build()));
    }

    @PutMapping("/moveDeleveryToTrash/")
    public Mono<Trash> moveDeleveryToTrash(@RequestBody Delevery delevery) {
        return this.deleverys.deleteById(delevery.getId())
                .then(this.trashs.save(Trash.builder().id(delevery.getId()).delevery(delevery).build()));
    }
}
