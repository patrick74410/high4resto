package fr.high4technology.high4resto.bean.Client;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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



    @GetMapping("/generateCommande/{idClient}/{securityKey}")
    public Mono<Commande> generateCommande(@PathVariable String idClient, @PathVariable String securityKey)
    {
      Queue<Stock> currentStock = new ConcurrentLinkedQueue<Stock>();
      Queue<Commande> commande = new ConcurrentLinkedQueue<Commande>();
      Queue<Client> cli=new ConcurrentLinkedQueue<Client>();

      return
      this.stocks.findAll().collectList().flatMap(list->{
          // Je met le stock en cache
          currentStock.addAll(list);
          return Mono.empty();
      }).
      // je compte les commandes pour attribue une nouvelle commande et je la sauvegarde et je la renvoie
      then(this.commandes.count().flatMap(count->{
          return this.commandes.save(Commande.builder().number(count).client(idClient).deleveryMode("click&collect").destination("outside").finish(false).inside(Util.getTimeNow()).build());
      }).flatMap(com->{
          commande.add(com);
          return Mono.empty();
      })).
      // je récupère le client et ce qui et ce qui a dans le panier
      thenMany(this.getById(idClient, securityKey)).flatMap(client->{
          cli.add(client);
          return Flux.fromIterable(client.getCurrentPanier());
      })
      // Pour chaque élément du panier je récupère l'élément de stock correspondant et j'en construit un Pre order
      .flatMap(item->{
        Stock tpStock;
          for (Stock stock:currentStock)
          {
            if(stock.getItem().getName().equals(item.getName()))
            {
                tpStock=stock;
                currentStock.remove(tpStock);
                return Mono.just(
                    PreOrder.builder().id(tpStock.getId()).inside(Util.getTimeNow()).destination("outside").orderNumber(commande.peek().getId()).idCustomer(idClient).stock(tpStock).build());
            }
          }
          return Mono.empty();
       })
       // Je sauvegarde le Préorder
      .flatMap(preOrders::save)
      // Je rajoute à la commande le Préorder et je le supprime de stock
      .flatMap(preOrder->{
          commande.peek().getItems().add(preOrder);
          return this.stocks.deleteById(preOrder.getStock().getId());
      })
      // Je sauvegarde la commande
      .then(commandes.save(commande.peek())).flatMap(com->{
          cli.peek().getCommandes().add(com);
          cli.peek().setCurrentPanier(new ArrayList<ItemCarte>());
          return clients.save(cli.peek());
      }).then(Mono.just(commande.peek()));

    }
    @GetMapping("/get/{idClient}/{securityKey}")
    public Mono<Client> getById(@PathVariable String idClient, @PathVariable String securityKey) {

        return this.security.findById(idClient).flatMap(security_user -> {
            if (security_user.getGenerateKey().equals(securityKey)) {
                return clients.findById(idClient);
            } else {
                return Mono.just(Client.builder().build());
            }
        }).map(client->{
            double price=0;
            for(ItemCarte itemCarte:client.getCurrentPanier())
            {
                price+=itemCarte.getPrice();
                for(OptionsItem options:itemCarte.getOptions())
                {
                    for(OptionItem choix:options.getOptions())
                    {
                        if(choix.isSelected())
                        {
                            price+=choix.getPrice();
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
        })
        .map(cc->{
            double price=0;
            for(ItemCarte itemCarte:cc.getCurrentPanier())
            {
                price+=itemCarte.getPrice();
                for(OptionsItem options:itemCarte.getOptions())
                {
                    for(OptionItem choix:options.getOptions())
                    {
                        if(choix.isSelected())
                        {
                            price+=choix.getPrice();
                        }
                    }
                }
            }
            cc.setPrice(price);
            return cc;
        }).flatMap(clients::save);
    }

}
