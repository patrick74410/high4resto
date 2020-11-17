package fr.high4technology.high4resto.bean.SecurityUser;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.high4technology.high4resto.bean.Client.Client;
import fr.high4technology.high4resto.bean.Client.ClientRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/authorize-me")
@RequiredArgsConstructor
public class SecurityUserController {
    private String securityKey="7mvSDB5P@gr4_8A%tNB7wdBeI7YgHwSs47M5EIttjfYFN46tVUY8C1B5za06OzXP8pxinJIwdcK1";
    @Autowired
    private SecurityUserRepository security;
    @Autowired
    private ClientRepository clients;

    @PostMapping("/connect/")
    Mono<SecurityUser> insert(@RequestBody SecurityUser user) {
        if (user.getIdentity().equals(this.securityKey))
            return security.save(user);
        else
            return Mono.just(user);
    }

    @PutMapping("/firstConnexion/")
    Mono<Client> firstConnexion(@RequestBody SecurityUser user)
    {
        if (user.getIdentity().equals(this.securityKey))
            return clients.save(Client.builder().email(user.getEmail()).id(user.getId()).firstConnexion(new Date()).build());
        else
            return Mono.just(Client.builder().id("anonymous").build());
    }

}
