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
                        .pathMatchers("/api/users/{user}/**").access(this::currentUserMatchesPath)
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .pathMatchers("/api/allergene/find/**").permitAll()
                        .pathMatchers("/api/allergene/delete/**").hasRole("ADMIN")
                        .pathMatchers("/api/allergene/insert/**").hasRole("ADMIN")
                        .pathMatchers("/api/allergene/update/**").hasRole("ADMIN")
                        .pathMatchers("/api/categorie/find/**").permitAll()
                        .pathMatchers("/api/categorie/delete/**").hasRole("ADMIN")
                        .pathMatchers("/api/categorie/insert/**").hasRole("ADMIN")
                        .pathMatchers("/api/categorie/update/**").hasRole("ADMIN")
                        .pathMatchers("/api/images/find/**").permitAll()
                        .pathMatchers("/api/images/delete/**").hasRole("ADMIN")
                        .pathMatchers("/api/images/insert/**").hasRole("ADMIN")
                        .pathMatchers("/api/images/update/**").hasRole("ADMIN")
                        .pathMatchers("/api/images/upload/**").hasRole("ADMIN")
                        .pathMatchers("/api/images/download/**").permitAll()
                        .pathMatchers("/api/ingredient/find/**").permitAll()
                        .pathMatchers("/api/ingredient/delete/**").hasRole("ADMIN")
                        .pathMatchers("/api/ingredient/insert/**").hasRole("ADMIN")
                        .pathMatchers("/api/ingredient/update/**").hasRole("ADMIN")
                        .pathMatchers("/api/itemCarte/find/**").permitAll()
                        .pathMatchers("/api/itemCarte/delete/**").hasRole("ADMIN")
                        .pathMatchers("/api/itemCarte/insert/**").hasRole("ADMIN")
                        .pathMatchers("/api/itemCarte/update/**").hasRole("ADMIN")
                        .pathMatchers("/api/tva/find/**").permitAll()
                        .pathMatchers("/api/tva/delete/**").hasRole("ADMIN")
                        .pathMatchers("/api/tva/insert/**").hasRole("ADMIN")
                        .pathMatchers("/api/tva/update/**").hasRole("ADMIN")
                        .pathMatchers("/api/optionsItem/find/**").permitAll()
                        .pathMatchers("/api/optionsItem/delete/**").hasRole("ADMIN")
                        .pathMatchers("/api/optionsItem/insert/**").hasRole("ADMIN")
                        .pathMatchers("/api/optionsItem/update/**").hasRole("ADMIN")
                        .pathMatchers("/api/promotion/find/**").permitAll()
                        .pathMatchers("/api/promotion/delete/**").hasRole("ADMIN")
                        .pathMatchers("/api/promotion/insert/**").hasRole("ADMIN")
                        .pathMatchers("/api/promotion/update/**").hasRole("ADMIN")                       
                        .pathMatchers("/api/horaire/find/**").permitAll()
                        .pathMatchers("/api/horaire/delete/**").hasRole("ADMIN")
                        .pathMatchers("/api/horaire/insert/**").hasRole("ADMIN")
                        .pathMatchers("/api/horaire/update/**").hasRole("ADMIN")
                        .pathMatchers("/api/identite/find/**").permitAll()
                        .pathMatchers("/api/identite/delete/**").hasRole("ADMIN")
                        .pathMatchers("/api/identite/insert/**").hasRole("ADMIN")
                        .pathMatchers("/api/identite/update/**").hasRole("ADMIN")
                        .pathMatchers("/api/articleCatetegorie/find/**").permitAll()
                        .pathMatchers("/api/articleCatetegorie/delete/**").hasRole("ADMIN")
                        .pathMatchers("/api/articleCatetegorie/insert/**").hasRole("ADMIN")
                        .pathMatchers("/api/articleCatetegorie/update/**").hasRole("ADMIN")                                    
                        .pathMatchers("/api/imageCategorie/find/**").permitAll()
                        .pathMatchers("/api/imageCategorie/delete/**").hasRole("ADMIN")
                        .pathMatchers("/api/imageCategorie/insert/**").hasRole("ADMIN")
                        .pathMatchers("/api/imageCategorie/update/**").hasRole("ADMIN")
                        .pathMatchers("/api/webConfig/find/**").permitAll()
                        .pathMatchers("/api/webConfig/delete/**").hasRole("ADMIN")
                        .pathMatchers("/api/webConfig/insert/**").hasRole("ADMIN")
                        .pathMatchers("/api/webConfig/update/**").hasRole("ADMIN")   
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
