package fr.high4technology.high4resto.bean.Client;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import fr.high4technology.high4resto.Util.Util;
import fr.high4technology.high4resto.bean.ItemCarte.ItemCarte;
import fr.high4technology.high4resto.bean.OptionItem.OptionItem;
import fr.high4technology.high4resto.bean.OptionItem.OptionsItem;
import fr.high4technology.high4resto.bean.SecurityUser.SecurityUserRepository;
import fr.high4technology.high4resto.bean.Stock.Stock;
import fr.high4technology.high4resto.bean.Stock.StockRepository;
import fr.high4technology.high4resto.bean.Tracability.PreOrder.PreOrder;
import fr.high4technology.high4resto.bean.Tracability.PreOrder.PreOrderRepository;
import fr.high4technology.high4resto.bean.commande.Commande;
import fr.high4technology.high4resto.bean.commande.CommandeRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientController {
    @Autowired
    private ClientRepository clients;
    @Autowired
    private SecurityUserRepository security;
    @Autowired
    private CommandeRepository commandes;
    @Autowired
    private PreOrderRepository preOrders;
    @Autowired
    private StockRepository stocks;

    public Mono<PreOrder> moveToPreorder(@RequestBody PreOrder preOrder) {
        preOrder.setInside(Util.getTimeNow());
        preOrder.setId(preOrder.getStock().getId());
        preOrder.getStock().getItem().setStock(1);
        return this.stocks.deleteById(preOrder.getStock().getId())
                .then(this.commandes.findById(preOrder.getOrderNumber()).map(result -> {
                    if (result.getItems().size() < 1)
                        result.setInside(Util.getTimeNow());
                    result.getItems().add(preOrder);
                    return result;
                }).flatMap(commandes::save)).then(this.preOrders.save(preOrder));
    }

    @GetMapping("/generateCommande/{idClient}/{securityKey}")
    public Mono<Commande> generateCommande(@PathVariable String idClient, @PathVariable String securityKey) {
        Queue<Stock> currentStock = new ConcurrentLinkedQueue<Stock>();
        AtomicReference<Commande> commande = new AtomicReference<Commande>();

        return this.stocks.findAll().collectList().flatMap(list -> {
            // Je met le stock en cache
            currentStock.addAll(list);
            return Mono.empty();
        }).then(this.commandes.count().flatMap(count -> {
            return this.commandes.save(Commande.builder().number(count).client(idClient).deleveryMode("click&collect")
                    .destination("outside").finish(false).inside(Util.getTimeNow()).build());
        })).flatMap(com -> {
            commande.set(com);
            return Mono.just(commande.get());
        }).then(this.stocks.findAll().collectList().flatMap(list -> {

            currentStock.addAll(list);
            return Mono.empty();
        })).then(this.getById(idClient, securityKey)).flatMap(cli -> {
            for (ItemCarte item : cli.getCurrentPanier()) {
                for (Stock stock : currentStock) {
                    if (item.getName().equals(stock.getItem().getName())) {
                        currentStock.remove(stock);
                        PreOrder preOrder = new PreOrder();
                        preOrder.setDestination("outside");
                        preOrder.setId(stock.getId());
                        preOrder.setIdCustomer(idClient);
                        preOrder.setInside(Util.getTimeNow());
                        preOrder.setOrderNumber(commande.get().getId());
                        preOrder.setStock(stock);
                        commande.get().getItems().add(preOrder);
                        cli.getCurrentPanier().remove(item);
                    }
                }
            }
            cli.getCommandes().add(commande.get());
            return Mono.just(cli);
        }).flatMap(clients::save).flatMapMany(cli -> {
            return Flux.fromIterable(commande.get().getItems());
        }).flatMap(preOrders::save).flatMap(preOrder -> {
            return this.stocks.deleteById(preOrder.getId());
        }).then(commandes.save(commande.get()));
    }

    @GetMapping("/get/{idClient}/{securityKey}")
    public Mono<Client> getById(@PathVariable String idClient, @PathVariable String securityKey) {

        return this.security.findById(idClient).flatMap(security_user -> {
            if (security_user.getGenerateKey().equals(securityKey)) {
                return clients.findById(idClient);
            } else {
                return Mono.just(Client.builder().build());
            }
        }).map(client -> {
            double price = 0;
            for (ItemCarte itemCarte : client.getCurrentPanier()) {
                price += itemCarte.getPrice();
                for (OptionsItem options : itemCarte.getOptions()) {
                    for (OptionItem choix : options.getOptions()) {
                        if (choix.isSelected()) {
                            price += choix.getPrice();
                        }
                    }
                }
            }
            client.setPrice(price);
            return client;
        });
    }

    @PutMapping("/update/{securityKey}")
    Mono<Client> update(@RequestBody Client client, @PathVariable String securityKey) {
        return this.security.findById(client.getId()).flatMap(security_user -> {
            if (security_user.getGenerateKey().equals(securityKey)) {
                return clients.findById(client.getId());
            } else {
                return Mono.just(Client.builder().id("anonymous").build());
            }
        }).map(foundItem -> {
            if (!foundItem.getId().equals("anonymous")) {
                foundItem.setAdresseL1(client.getAdresseL1());
                foundItem.setAdresseL2(client.getAdresseL2());
                foundItem.setCity(client.getCity());
                foundItem.setZip(client.getZip());
                foundItem.setCurrentPanier(client.getCurrentPanier());
                foundItem.setEmail(client.getEmail());
                foundItem.setName(client.getName());
                foundItem.setLastName(client.getLastName());
                foundItem.setSendInfo(client.isSendInfo());
            }
            return foundItem;
        }).map(cc -> {
            double price = 0;
            for (ItemCarte itemCarte : cc.getCurrentPanier()) {
                price += itemCarte.getPrice();
                for (OptionsItem options : itemCarte.getOptions()) {
                    for (OptionItem choix : options.getOptions()) {
                        if (choix.isSelected()) {
                            price += choix.getPrice();
                        }
                    }
                }
            }
            cc.setPrice(price);
            return cc;
        }).flatMap(clients::save);
    }

}
