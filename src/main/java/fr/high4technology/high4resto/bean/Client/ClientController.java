package fr.high4technology.high4resto.bean.Client;

import java.util.ArrayList;

import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import fr.high4technology.high4resto.Util.Util;
import fr.high4technology.high4resto.Util.Variable;
import fr.high4technology.high4resto.bean.Concurrency;
import fr.high4technology.high4resto.bean.ItemCarte.ItemCarte;
import fr.high4technology.high4resto.bean.OptionItem.OptionItem;
import fr.high4technology.high4resto.bean.OptionItem.OptionsItem;
import fr.high4technology.high4resto.bean.SecurityUser.SecurityUserRepository;
import fr.high4technology.high4resto.bean.Stock.Stock;
import fr.high4technology.high4resto.bean.Stock.StockRepository;
import fr.high4technology.high4resto.bean.Struct.Gps;
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
    private PreOrderRepository preOrders;
    @Autowired
    private StockRepository stocks;
    @Autowired
    private CommandeRepository commandes;

    private GeoApiContext context = new GeoApiContext.Builder()
    .apiKey(Variable.geoApi).build();


    private Mono<PreOrder> retriveItemFromStock(ItemCarte item,String destination,String idCustomer,String orderNumber)
    {
        final PreOrder preOrd=PreOrder.builder().id("anonymous").stock(Stock.builder().item(ItemCarte.builder().name("fake").build()).build()).build();
        return this.stocks.findAll().filter(s->s.getItem().getName().equals(item.getName())).collectList()
        .flatMap(result->{

            for(Stock stock:result)
            {
                if(!Concurrency.mapStock.containsKey(stock.getId()))
                {
                    Concurrency.mapStock.put(stock.getId(), 1);
                    preOrd.setId(stock.getId());
                    preOrd.setDestination(destination);
                    preOrd.setIdCustomer(idCustomer);
                    preOrd.setInside(Util.getTimeNow());
                    preOrd.setOrderNumber(orderNumber);
                    preOrd.setStock(stock);
                    preOrd.getStock().setItem(item);
                    return this.stocks.deleteById(stock.getId());
                }
            }
            return Mono.empty();

        }).then(preOrders.save(preOrd));

    }

    @GetMapping("/generateCommande/{idClient}/{securityKey}/{mode}")
    public Mono<Client> generateCommande(@PathVariable String idClient, @PathVariable String securityKey,@PathVariable String mode) {
        final Commande commande=new Commande();
        final Client clientC=new Client();
        return
        this.commandes.count().flatMap(count->{
            commande.setNumber(count);
            return this.commandes.save(commande);
        }).flatMap(coma->{
            commande.setId(coma.getId());
            commande.setClient(idClient);
            commande.setDestination("outside");
            commande.setInside(Util.getTimeNow());
            commande.setMandatory(idClient);
            commande.setStatus("onProcess");
            commande.setDeleveryMode(mode);
            commande.setFinish(false);
            return Mono.empty();
        }).then(
        this.getById(idClient, securityKey))
        .flatMapMany(client -> {
            clientC.setAdresseL1(client.getAdresseL1());
            clientC.setAdresseL2(client.getAdresseL2());
            clientC.setZip(client.getZip());
            clientC.setCity(client.getCity());
            GeocodingApiRequest req = GeocodingApi.newRequest(context).address(clientC.getAdresseL1()+" "+clientC.getAdresseL2()+" "+clientC.getZip()+" "+clientC.getCity());
            DirectionsApiRequest directionRequest = DirectionsApi.newRequest(context);
            directionRequest.origin(new LatLng(Variable.gps.getLatitude(),Variable.gps.getLongitude()));
            DirectionsResult route;
            GeocodingResult[] results;
            try {
                results=req.await();
                directionRequest.destination(results[0].geometry.location);
                directionRequest.mode(TravelMode.DRIVING);
                route=directionRequest.await();
                commande.setDistanceTime(route.routes[0].legs[0].duration.humanReadable);
                commande.setDistance(route.routes[0].legs[0].distance.inMeters);
                clientC.setGps(Gps.builder().latitude(results[0].geometry.location.lat).longitude(results[0].geometry.location.lng).build());
            } catch (Exception e) {
            }
            clientC.setCommande(client.getCommande());
            clientC.setCurrentPanier(client.getCurrentPanier());
            clientC.setEmail(client.getEmail());
            clientC.setFirstConnexion(client.getFirstConnexion());
            clientC.setLastConnexion(client.getLastConnexion());
            clientC.setLastName(client.getLastName());
            clientC.setName(client.getName());
            clientC.setPrice(client.getPrice());
            clientC.setSendInfo(client.isSendInfo());
            clientC.setId(client.getId());
            return Flux.fromIterable(client.getCurrentPanier());
        }).flatMap(item -> {
            return this.retriveItemFromStock(item, "outside", idClient, commande.getId());
        }).collectList()
                .flatMap(list -> {
                    for(PreOrder preOrder:list)
                    {
                        var index=0;
                        for(ItemCarte item:clientC.getCurrentPanier())
                        {
                            if(item.getName().equals(preOrder.getStock().getItem().getName()))
                            {
                                clientC.getCurrentPanier().remove(index);
                                break;
                            }
                            index+=1;
                        }
                    }
                    list.removeIf(a->a.getId().equals("anonymous"));
                    ArrayList<PreOrder> fListe=new ArrayList<PreOrder>();
                    for(PreOrder preOrder:list)
                    {
                        PreOrder tp=preOrder;
                        try{
                            tp.getStock().setItem(tp.getStock().getItem().finalPrice(Util.getTimeNow()));
                        }
                        catch(Exception e)
                        {
                            e.getMessage();
                        }
                        fListe.add(tp);
                    }

                    commande.setItems(fListe);
                    clientC.setCommande(commande);
                    return clients.save(clientC);
                });
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
