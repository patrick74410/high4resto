package fr.high4technology.high4resto;

import fr.high4technology.high4resto.bean.user.*;
import fr.high4technology.high4resto.bean.Allergene.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository users;
    private final AllergenRepository allergenes;
    private final PasswordEncoder passwordEncoder;

    @EventListener(value = ApplicationReadyEvent.class)
    public void init() {
        log.info("start data initialization...");
        var initAllergenes=this.allergenes.findAll().switchIfEmpty(Flux.just(
                "Céréales contenant du gluten : blé, seigle, orge, avoine, épeautre, kamut…",
                "Arachides",
                "Fruits à coque : noix, amandes, noisettes, noix de cajou, noix de pécan, pistaches, etc.",
                "Œufs",
                "Poissons",
                "Soja",
                "lait",
                "Crustacés",
                "Mollusque",
                "Céléri",
                "Moutarde",
                "Graine de sésame",
                "Lupin",
                " Dioxyde de soufre (SO2) et sulfites à des concentrations supérieures à 10 mg/kg ou à 10 mg/litre")
                        .flatMap(name ->{
                                Allergene allergene= Allergene.builder()
                                .name(name)
                                .build();
                        return this.allergenes.save(allergene);
                        })
                );

        var initUsers=this.users.findAll()
                .switchIfEmpty(
                        Flux.just("user", "admin")
                                .flatMap(username -> {
                                    List<String> roles = "user".equals(username) ?
                                            Arrays.asList("ROLE_USER") : Arrays.asList("ROLE_USER", "ROLE_ADMIN");

                                    User user = User.builder()
                                            .roles(roles)
                                            .username(username)
                                            .password(passwordEncoder.encode("iddadidkfq"))
                                            .email(username + "@example.com")
                                            .build();

                                    return this.users.save(user);
                                })
                );

                initUsers.doOnSubscribe(data -> log.info("data:" + data))
                .thenMany(initUsers)
                .subscribe(
                        data -> log.info("data:" + data), err -> log.error("error:" + err),
                        () -> log.info("done initialization...")
                );
                initAllergenes.doOnSubscribe(data -> log.info("data:"+data))
                .thenMany(initAllergenes)
                .subscribe(
                        data -> log.info("data:" + data), err -> log.error("error:" + err),
                        () -> log.info("done initialization...")
                );

    }

}
