package fr.high4technology.high4resto.config;

import fr.high4technology.high4resto.bean.user.UserRepository;
import fr.high4technology.high4resto.security.jwt.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http,
                                                JwtTokenProvider tokenProvider,
                                                ReactiveAuthenticationManager reactiveAuthenticationManager) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authenticationManager(reactiveAuthenticationManager)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(it -> it
                        .pathMatchers("/users/{user}/**").access(this::currentUserMatchesPath)
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .pathMatchers("/allergene/find/**").permitAll()
                        .pathMatchers("/allergene/delete/**").hasRole("ADMIN")
                        .pathMatchers("/allergene/insert/**").hasRole("ADMIN")
                        .pathMatchers("/allergene/update/**").hasRole("ADMIN")
                        .pathMatchers("/categorie/find/**").permitAll()
                        .pathMatchers("/categorie/delete/**").hasRole("ADMIN")
                        .pathMatchers("/categorie/insert/**").hasRole("ADMIN")
                        .pathMatchers("/categorie/update/**").hasRole("ADMIN")
                        .pathMatchers("/images/find/**").permitAll()
                        .pathMatchers("/images/delete/**").hasRole("ADMIN")
                        .pathMatchers("/images/insert/**").hasRole("ADMIN")
                        .pathMatchers("/images/update/**").hasRole("ADMIN")
                        .pathMatchers("/images/upload/**").hasRole("ADMIN")
                        .pathMatchers("/images/download/**").permitAll()
                        .pathMatchers("/ingredient/find/**").permitAll()
                        .pathMatchers("/ingredient/delete/**").hasRole("ADMIN")
                        .pathMatchers("/ingredient/insert/**").hasRole("ADMIN")
                        .pathMatchers("/ingredient/update/**").hasRole("ADMIN")
                        .pathMatchers("/itemCarte/find/**").permitAll()
                        .pathMatchers("/itemCarte/delete/**").hasRole("ADMIN")
                        .pathMatchers("/itemCarte/insert/**").hasRole("ADMIN")
                        .pathMatchers("/itemCarte/update/**").hasRole("ADMIN")
                        .pathMatchers("/tva/find/**").permitAll()
                        .pathMatchers("/tva/delete/**").hasRole("ADMIN")
                        .pathMatchers("/tva/insert/**").hasRole("ADMIN")
                        .pathMatchers("/tva/update/**").hasRole("ADMIN")
                        .pathMatchers("/optionsItem/find/**").permitAll()
                        .pathMatchers("/optionsItem/delete/**").hasRole("ADMIN")
                        .pathMatchers("/optionsItem/insert/**").hasRole("ADMIN")
                        .pathMatchers("/optionsItem/update/**").hasRole("ADMIN")
                        .pathMatchers("/promotion/find/**").permitAll()
                        .pathMatchers("/promotion/delete/**").hasRole("ADMIN")
                        .pathMatchers("/promotion/insert/**").hasRole("ADMIN")
                        .pathMatchers("/promotion/update/**").hasRole("ADMIN")                       
                        .pathMatchers("/album/find/**").permitAll()
                        .pathMatchers("/album/delete/**").hasRole("ADMIN")
                        .pathMatchers("/album/insert/**").hasRole("ADMIN")
                        .pathMatchers("/album/update/**").hasRole("ADMIN")
                        .pathMatchers("/horaire/find/**").permitAll()
                        .pathMatchers("/horaire/delete/**").hasRole("ADMIN")
                        .pathMatchers("/horaire/insert/**").hasRole("ADMIN")
                        .pathMatchers("/horaire/update/**").hasRole("ADMIN")
                        .pathMatchers("/identite/find/**").permitAll()
                        .pathMatchers("/identite/delete/**").hasRole("ADMIN")
                        .pathMatchers("/identite/insert/**").hasRole("ADMIN")
                        .pathMatchers("/identite/update/**").hasRole("ADMIN")
                        .pathMatchers("/articleCatetegorie/find/**").permitAll()
                        .pathMatchers("/articleCatetegorie/delete/**").hasRole("ADMIN")
                        .pathMatchers("/articleCatetegorie/insert/**").hasRole("ADMIN")
                        .pathMatchers("/articleCatetegorie/update/**").hasRole("ADMIN")                                    
                        .anyExchange().permitAll()
                )
                .addFilterAt(new JwtTokenAuthenticationFilter(tokenProvider), SecurityWebFiltersOrder.HTTP_BASIC)
                .build();


    }

    private Mono<AuthorizationDecision> currentUserMatchesPath(Mono<Authentication> authentication,
                                                               AuthorizationContext context) {
        return authentication
                .map(a -> context.getVariables().get("user").equals(a.getName()))
                .map(AuthorizationDecision::new);
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService(UserRepository users) {

        return username -> users.findByUsername(username)
                .map(u -> User
                        .withUsername(u.getUsername()).password(u.getPassword())
                        .authorities(u.getRoles().toArray(new String[0]))
                        .accountExpired(!u.isActive())
                        .credentialsExpired(!u.isActive())
                        .disabled(!u.isActive())
                        .accountLocked(!u.isActive())
                        .build()
                );
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService,
                                                                       PasswordEncoder passwordEncoder) {
        var authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder);
        return authenticationManager;
    }

}
