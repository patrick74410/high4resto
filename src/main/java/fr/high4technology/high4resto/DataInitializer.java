package fr.high4technology.high4resto;

import fr.high4technology.high4resto.bean.user.*;
import fr.high4technology.high4resto.bean.Allergene.*;
/*
import fr.high4technology.high4resto.bean.Horaire.Horaire;
import fr.high4technology.high4resto.bean.Horaire.HoraireRepository;
import fr.high4technology.high4resto.bean.ItemPlaning.ItemPlaning;
import fr.high4technology.high4resto.bean.Stock.Stock;
import fr.high4technology.high4resto.bean.Stock.StockRepository;
import fr.high4technology.high4resto.bean.Tracability.PreOrder.PreOrder;
import fr.high4technology.high4resto.bean.commande.Commande;
import fr.high4technology.high4resto.Util.Util;
import java.util.concurrent.atomic.AtomicReference;
import java.util.LinkedList;

*/
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer {

        private final UserRepository users;
        private final AllergenRepository allergenes;
        private final PasswordEncoder passwordEncoder;
/*
        private final StockRepository stocks;
        private final HoraireRepository horaires;
*/
        @EventListener(value = ApplicationReadyEvent.class)
        public void init() {
                log.info("start data initialization...");
                var initAllergenes = this.allergenes.findAll().switchIfEmpty(Flux.just(
                                "Céréales contenant du gluten : blé, seigle, orge, avoine, épeautre, kamut…",
                                "Arachides",
                                "Fruits à coque : noix, amandes, noisettes, noix de cajou, noix de pécan, pistaches, etc.",
                                "Œufs", "Poissons", "Soja", "lait", "Crustacés", "Mollusque", "Céléri", "Moutarde",
                                "Graine de sésame", "Lupin",
                                " Dioxyde de soufre (SO2) et sulfites à des concentrations supérieures à 10 mg/kg ou à 10 mg/litre")
                                .flatMap(name -> {
                                        Allergene allergene = Allergene.builder().name(name).build();
                                        return this.allergenes.save(allergene);
                                }));

                var initUsers = this.users.findAll().switchIfEmpty(Mono.just("admin").flatMap(username -> {
                        List<String> roles = new ArrayList<String>();
                        roles.add("ROLE_ADMIN");
                        roles.add("ROLE_MANAGER");
                        roles.add("ROLE_WINESTEWARD");
                        roles.add("ROLE_BARWAITER");
                        roles.add("ROLE_SERVER");
                        roles.add("ROLE_DELEVERYMAN");
                        roles.add("ROLE_HOTCOOK");
                        roles.add("ROLE_COLDCOOK");
                        roles.add("ROLE_COOK");
                        roles.add("ROLE_EDITOR");

                        User admin = User.builder().roles(roles).email("").active(true)
                                        .password(passwordEncoder.encode("myPassword")).username("admin").build();

                        return Mono.just(admin);
                }).flatMap(users::save));

                initUsers.doOnSubscribe(data -> log.info("data:" + data)).thenMany(initUsers).subscribe(
                                data -> log.info("data:" + data), err -> log.error("error:" + err),
                                () -> log.info("done initialization..."));
                initAllergenes.doOnSubscribe(data -> log.info("data:" + data)).thenMany(initAllergenes).subscribe(
                                data -> log.info("data:" + data), err -> log.error("error:" + err),
                                () -> log.info("done initialization..."));

                /* AtomicReference<Horaire> horaireC = new AtomicReference<Horaire>();


                this.horaires.findAll().flatMap(horaires->{
                        horaireC.set(horaires);
                        return Mono.empty();
                    }).then(stocks.findAll().collectList().flatMap(stocks -> {
                        Commande commande = Commande.builder().items(new ArrayList<PreOrder>()).client("test")
                                        .deleveryMode("click&collect").number(1).mandatory("demo").build();
                        for (Stock stock : stocks) {
                                PreOrder tp = PreOrder.builder().destination("outside").orderNumber("1").stock(stock)
                                                .build();
                                int r = (int) (Math.random() * 30);

                                if(r==10)
                                commande.getItems().add(tp);
                        }
                        for (PreOrder preOrder : commande.getItems()) {
                                log.warn(preOrder.getStock().getItem().getName());
                        }
                        ItemPlaning planning=ItemPlaning.builder().id(Util.getStringSimpleDateNow()).a(horaireC.get().generatePlanning()).build();
                        LinkedList<Double> commandeTrame= commande.generateMicroPlanning();
                        int begin=12*60-commandeTrame.size();

                        for(int i=begin;i!=planning.getA().length-commandeTrame.size();i++)
                        {
                            boolean goodPlace=true;
                            for(int j=0;j!=commandeTrame.size();j++)
                            {
                                if(commandeTrame.get(j)+planning.getA()[i]>1.0)
                                {
                                    goodPlace=false;

                                }
                            }
                            if(goodPlace)
                            {
                                int heure=(i+commandeTrame.size())/60;
                                int minute=(i+commandeTrame.size())%60;
                                log.warn(Integer.toString(heure)+":"+Integer.toString(minute));
                                break;
                            }
                        }
                        return Mono.just(commande);
                })).subscribe();
                */
        }

}
