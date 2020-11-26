package fr.high4technology.high4resto.bean.user;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import javax.validation.Valid;

import fr.high4technology.high4resto.security.jwt.JwtTokenProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;


import fr.high4technology.high4resto.web.AuthenticationRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")

public class UserController {

    private final JwtTokenProvider tokenProvider;
    private final ReactiveAuthenticationManager authenticationManager;

    @Autowired
    private UserRepository users;

    @PostMapping("/login")
    public Mono<ResponseEntity<Map<String, String>>> login(
            @Valid @RequestBody Mono<AuthenticationRequest> authRequest) {

        return authRequest.flatMap(login -> this.authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword()))
                .map(this.tokenProvider::createToken)).map(jwt -> {
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
                    var tokenBody = Map.of("access_token", jwt);
                    return new ResponseEntity<>(tokenBody, httpHeaders, HttpStatus.OK);
                }).onErrorReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }

    @GetMapping("/me")
    public Mono<User> current(@AuthenticationPrincipal Mono<UserDetails> principal) {
        return principal.flatMap(pri->{
            return users.findByUsername(pri.getUsername());
        }).map(user->{
            user.setPassword("");
            return user;
        });
    }

}
