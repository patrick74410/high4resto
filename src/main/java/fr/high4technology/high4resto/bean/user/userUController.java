package fr.high4technology.high4resto.bean.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/config-users")

public class userUController {
    @Autowired
    private final UserRepository users;

    private final PasswordEncoder passwordEncoder;

    @GetMapping("/find/")
    public Flux<User> getAll() {
        return this.users.findAll().filter(user -> !user.getUsername().equals("admin"));
    }

    @GetMapping("/getAllRole/")
    public Flux<Object> getAllRole() {
        return this.users.findAll().filter(user -> user.getUsername().equals("admin")).flatMap(admin -> {
            return Flux.fromIterable(admin.getRoles());
        });
    }

    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return users.deleteById(id).map(r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.ok().<Void>build());
    }

    @PutMapping("/updatePassword/")
    Mono<User> updatePassword(@RequestBody User user) {
        return this.users.findById(user.getId()).map(foundItem -> {
            foundItem.setPassword(passwordEncoder.encode(user.getPassword()));
            return foundItem;
        }).flatMap(users::save);
    }

    @PutMapping("/updateRole/")
    Mono<User> updateRole(@RequestBody User user) {
        return this.users.findById(user.getId()).map(foundItem -> {
            foundItem.setRoles(user.getRoles());
            ;
            return foundItem;
        }).flatMap(users::save);
    }

    @PutMapping("/insert/")
    Mono<User> insert(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return this.users.save(user);
    }

}
