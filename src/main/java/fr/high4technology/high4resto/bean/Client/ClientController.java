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


    private Mono<PreOrder> retriveItemFromStock(String item,String destination,String idCustomer,String orderNumber)
    {
        final PreOrder preOrd=new PreOrder();
        return this.stocks.findAll().filter(s->s.getItem().getName().equals(item)).collectList()
        .flatMap(result->{
            Stock tpStock=result.get(0);
            preOrd.setId(tpStock.getId());
            preOrd.setDestination(destination);
            preOrd.setIdCustomer(idCustomer);
            preOrd.setInside(Util.getTimeNow());
            preOrd.setOrderNumber(orderNumber);
            preOrd.setStock(tpStock);
            return preOrders.save(preOrd);
        })
        .flatMap(preOrder->{

            return this.stocks.deleteById(preOrder.getStock().getId());
        })
        .then(Mono.just(preOrd));
    }

    @GetMapping("/generateCommande/{idClient}/{securityKey}")
    public Mono<Commande> generateCommande(@PathVariable String idClient, @PathVariable String securityKey) {
        final Commande commande=new Commande();
        final Client clientC=new Client();

        return this.commandes.count()
        .flatMap(count->{
            commande.setNumber(count);
            return commandes.save(commande);
        })
        .flatMap(com->{
            commande.setId(com.getId());
            return this.getById(idClient, securityKey);
        })
        .flatMapMany(client -> {
            clientC.setAdresseL1(client.getAdresseL1());
            clientC.setAdresseL2(client.getAdresseL2());
            clientC.setCity(client.getCity());
            clientC.setCommandes(client.getCommandes());
            clientC.setCurrentPanier(client.getCurrentPanier());
            clientC.setEmail(client.getEmail());
            clientC.setFirstConnexion(client.getFirstConnexion());
            clientC.setLastConnexion(client.getLastConnexion());
            clientC.setLastName(client.getLastName());
            clientC.setName(client.getName());
            clientC.setPrice(client.getPrice());
            clientC.setSendInfo(client.isSendInfo());
            clientC.setZip(client.getZip());
            return Flux.fromIterable(client.getCurrentPanier());
        }).flatMap(item -> {
            return this.retriveItemFromStock(item.getName(), "outside", idClient, commande.getId());
        }).collectList()
                .flatMap(list -> {
                    for(PreOrder preOrder:list)
                    {
                        clientC.getCurrentPanier().removeIf((fi)->fi.getName().equals(preOrder.getStock().getItem().getName()));
                    }
                    commande.setItems(list);
                    return clients.save(clientC);
                })
                .then(commandes.save(commande));
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
