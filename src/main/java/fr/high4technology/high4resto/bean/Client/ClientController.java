package fr.high4technology.high4resto.bean.Client;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;

import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.model.DirectionsResult;
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
import fr.high4technology.high4resto.bean.Horaire.Horaire;
import fr.high4technology.high4resto.bean.Horaire.HoraireRepository;
import fr.high4technology.high4resto.bean.ItemCarte.ItemCarte;
import fr.high4technology.high4resto.bean.ItemPlaning.ItemPlaning;
import fr.high4technology.high4resto.bean.ItemPlaning.ItemPlaningRepository;
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
@RequestMapping("/"+Variable.apiPath+"/client")
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
    @Autowired
    private ItemPlaningRepository plannings;
    @Autowired
    private HoraireRepository horaires;

    private GeoApiContext context = new GeoApiContext.Builder().apiKey(Variable.geoApi).build();

    private Mono<PreOrder> retriveItemFromStock(ItemCarte item, String destination, String idCustomer,
            String orderNumber) {
        final PreOrder preOrd = PreOrder.builder().id("anonymous")
                .stock(Stock.builder().item(ItemCarte.builder().name("fake").build()).build()).build();
        return this.stocks.findAll().filter(s -> s.getItem().getName().equals(item.getName())).collectList()
                .flatMap(result -> {

                    for (Stock stock : result) {
                        if (!Concurrency.mapStock.containsKey(stock.getId())) {
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

    @GetMapping("/checkPlanning/{idClient}/{securityKey}/{hourBegin}")
    public Mono<Client> checkPlanning(@PathVariable String idClient, @PathVariable String securityKey,
            @PathVariable String hourBegin) {
        AtomicReference<Client> clientC = new AtomicReference<Client>();
        AtomicReference<Horaire> horaireC = new AtomicReference<Horaire>();
        return
        this.horaires.findAll().flatMap(horaires->{
            horaireC.set(horaires);
            return Mono.empty();
        })
        .then(
        this.getById(idClient, securityKey).flatMap(client -> {
            clientC.set(client);
            return Mono.just(clientC.get());
        }))
        .then(
            plannings.findById(Util.getStringSimpleDateNow())
        )
        .switchIfEmpty(
            plannings.save(ItemPlaning.builder().id(Util.getStringSimpleDateNow()).a(horaireC.get().generatePlanning()).build())
            )
        .flatMap(planning->{
            LinkedList<Double> commandeTrame= clientC.get().getCommande().generateMicroPlanning();
            int begin=Integer.parseInt(hourBegin.split(":")[0])*60+Integer.parseInt(hourBegin.split(":")[1])-commandeTrame.size();
            for(int i=begin;i!=planning.getA().length-commandeTrame.size();i++)
            {
                boolean goodPlace=true;
                for(int j=0;j!=commandeTrame.size();j++)
                {
                    if(commandeTrame.get(j)+planning.getA()[i]>0)
                    {
                        goodPlace=false;
                        break;
                    }
                }
                if(goodPlace)
                {
                    int heure=(i+commandeTrame.size())/60;
                    int minute=(i+commandeTrame.size())%60;
                    clientC.get().getCommande().setTimeToTake(Integer.toString(heure)+":"+Integer.toString(minute));
                    break;
                }
            }
            return clients.save(clientC.get());
        });
    }

    @GetMapping("/generateCommande/{idClient}/{securityKey}/{mode}")
    public Mono<Client> generateCommande(@PathVariable String idClient, @PathVariable String securityKey,
            @PathVariable String mode) {
        AtomicReference<Commande> commande = new AtomicReference<Commande>();
        AtomicReference<Client> clientC = new AtomicReference<Client>();
        return this.commandes.count().flatMap(count -> {
            commande.set(Commande.builder().build());
            commande.get().setNumber(count);
            return this.commandes.save(commande.get());
        }).flatMap(coma -> {
            commande.get().setId(coma.getId());
            commande.get().setClient(idClient);
            commande.get().setDestination("outside");
            commande.get().setInside(Util.getTimeNow());
            commande.get().setMandatory(idClient);
            commande.get().setStatus("onProcess");
            commande.get().setDeleveryMode(mode);
            commande.get().setFinish(false);
            return Mono.empty();
        }).then(this.getById(idClient, securityKey)).flatMapMany(client -> {
            clientC.set(client);
            GeocodingApiRequest req = GeocodingApi.newRequest(context).address(clientC.get().getAdresseL1() + " "
                    + clientC.get().getAdresseL2() + " " + clientC.get().getZip() + " " + clientC.get().getCity());
            DirectionsApiRequest directionRequest = DirectionsApi.newRequest(context);
            directionRequest.origin(new LatLng(Variable.gps.getLatitude(), Variable.gps.getLongitude()));
            DirectionsResult route;
            GeocodingResult[] results;
            try {
                results = req.await();
                directionRequest.destination(results[0].geometry.location);
                directionRequest.mode(TravelMode.DRIVING);
                route = directionRequest.await();
                commande.get().setDistanceTime(route.routes[0].legs[0].duration.inSeconds);
                commande.get().setDistance(route.routes[0].legs[0].distance.inMeters);
                clientC.get().setGps(Gps.builder().latitude(results[0].geometry.location.lat)
                        .longitude(results[0].geometry.location.lng).build());
            } catch (Exception e) {
            }
            return Flux.fromIterable(client.getCurrentPanier());
        }).flatMap(item -> {
            return this.retriveItemFromStock(item, "outside", idClient, commande.get().getId());
        }).collectList().flatMap(list -> {
            for (PreOrder preOrder : list) {
                var index = 0;
                for (ItemCarte item : clientC.get().getCurrentPanier()) {
                    if (item.getName().equals(preOrder.getStock().getItem().getName())) {
                        clientC.get().getCurrentPanier().remove(index);
                        break;
                    }
                    index += 1;
                }
            }
            list.removeIf(a -> a.getId().equals("anonymous"));
            ArrayList<PreOrder> fListe = new ArrayList<PreOrder>();
            for (PreOrder preOrder : list) {
                PreOrder tp = preOrder;
                try {
                    tp.getStock().setItem(tp.getStock().getItem().finalPrice(Util.getTimeNow()));
                } catch (Exception e) {
                    e.getMessage();
                }
                fListe.add(tp);
            }

            commande.get().setItems(fListe);
            clientC.get().setCommande(commande.get());
            return clients.save(clientC.get());
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
