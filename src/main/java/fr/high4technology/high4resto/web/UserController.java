package fr.high4technology.high4resto.web;

import fr.high4technology.high4resto.bean.user.User;
import fr.high4technology.high4resto.bean.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author hantsy
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository users;

    @GetMapping("/users/{username}")
    public Mono<User> get(@PathVariable() String username) {
        return this.users.findByUsername(username);
    }

}
