package fr.high4technology.high4resto.bean.SecurityUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/authorize-me")
@RequiredArgsConstructor
public class SecurityUserController {
    @Autowired
    private SecurityUserRepository security;
    @PostMapping("/connect/")
    Mono<SecurityUser> insert(@RequestBody SecurityUser user)
    {
        return security.save(user);
    }
   
}
