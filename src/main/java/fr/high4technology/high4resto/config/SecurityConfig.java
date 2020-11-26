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
        SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http, JwtTokenProvider tokenProvider,
                        ReactiveAuthenticationManager reactiveAuthenticationManager) {
                return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                                .authenticationManager(reactiveAuthenticationManager)
                                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                                .authorizeExchange(it -> it
                                                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                                                .pathMatchers("/api/config-user/**").hasRole("ADMIN")
                                                .pathMatchers("/api/config-users/**").hasRole("ADMIN")
                                                .pathMatchers("/api/allergene/find/**").permitAll()
                                                .pathMatchers("/api/allergene/delete/**").hasRole("MANAGER")
                                                .pathMatchers("/api/allergene/insert/**").hasRole("MANAGER")
                                                .pathMatchers("/api/allergene/update/**").hasRole("MANAGER")
                                                .pathMatchers("/api/categorie/find/**").permitAll()
                                                .pathMatchers("/api/categorie/delete/**").hasRole("MANAGER")
                                                .pathMatchers("/api/categorie/insert/**").hasRole("MANAGER")
                                                .pathMatchers("/api/categorie/update/**").hasRole("MANAGER")
                                                .pathMatchers("/api/images/find/**").permitAll()
                                                .pathMatchers("/api/images/delete/**").hasRole("MANAGER")
                                                .pathMatchers("/api/images/insert/**").hasRole("EDITOR")
                                                .pathMatchers("/api/images/update/**").hasRole("EDITOR")
                                                .pathMatchers("/api/images/upload/**").hasRole("EDITOR")
                                                .pathMatchers("/api/images/download/**").permitAll()
                                                .pathMatchers("/api/ingredient/find/**").permitAll()
                                                .pathMatchers("/api/ingredient/delete/**").hasRole("MANAGER")
                                                .pathMatchers("/api/ingredient/insert/**").hasRole("MANAGER")
                                                .pathMatchers("/api/ingredient/update/**").hasRole("MANAGER")
                                                .pathMatchers("/api/itemCarte/find/**").permitAll()
                                                .pathMatchers("/api/itemCarte/delete/**").hasRole("MANAGER")
                                                .pathMatchers("/api/itemCarte/insert/**").hasRole("MANAGER")
                                                .pathMatchers("/api/itemCarte/update/**").hasRole("MANAGER")
                                                .pathMatchers("/api/tva/find/**").permitAll()
                                                .pathMatchers("/api/tva/delete/**").hasRole("MANAGER")
                                                .pathMatchers("/api/tva/insert/**").hasRole("MANAGER")
                                                .pathMatchers("/api/tva/update/**").hasRole("MANAGER")
                                                .pathMatchers("/api/optionsItem/find/**").permitAll()
                                                .pathMatchers("/api/optionsItem/delete/**").hasRole("MANAGER")
                                                .pathMatchers("/api/optionsItem/insert/**").hasRole("MANAGER")
                                                .pathMatchers("/api/optionsItem/update/**").hasRole("MANAGER")
                                                .pathMatchers("/api/promotion/find/**").permitAll()
                                                .pathMatchers("/api/promotion/delete/**").hasRole("MANAGER")
                                                .pathMatchers("/api/promotion/insert/**").hasRole("MANAGER")
                                                .pathMatchers("/api/promotion/update/**").hasRole("MANAGER")
                                                .pathMatchers("/api/horaire/find/**").permitAll()
                                                .pathMatchers("/api/horaire/delete/**").hasRole("MANAGER")
                                                .pathMatchers("/api/horaire/insert/**").hasRole("MANAGER")
                                                .pathMatchers("/api/horaire/update/**").hasRole("MANAGER")
                                                .pathMatchers("/api/identite/find/**").permitAll()
                                                .pathMatchers("/api/identite/delete/**").hasRole("MANAGER")
                                                .pathMatchers("/api/identite/insert/**").hasRole("MANAGER")
                                                .pathMatchers("/api/identite/update/**").hasRole("MANAGER")
                                                .pathMatchers("/api/articleCategorie/find/**").permitAll()
                                                .pathMatchers("/api/articleCategorie/delete/**").hasRole("MANAGER")
                                                .pathMatchers("/api/articleCategorie/insert/**").hasRole("EDITOR")
                                                .pathMatchers("/api/articleCategorie/update/**").hasRole("EDITOR")
                                                .pathMatchers("/api/imageCategorie/find/**").permitAll()
                                                .pathMatchers("/api/imageCategorie/delete/**").hasRole("MANAGER")
                                                .pathMatchers("/api/imageCategorie/insert/**").hasRole("EDITOR")
                                                .pathMatchers("/api/imageCategorie/update/**").hasRole("EDITOR")
                                                .pathMatchers("/api/webConfig/find/**").permitAll()
                                                .pathMatchers("/api/webConfig/delete/**").hasRole("MANAGER")
                                                .pathMatchers("/api/webConfig/insert/**").hasRole("MANAGER")
                                                .pathMatchers("/api/webConfig/update/**").hasRole("MANAGER")
                                                .pathMatchers("/api/stock/**").hasRole("MANAGER")
                                                .pathMatchers("/api/itemDisponibility/**").hasRole("MANAGER")
                                                .anyExchange().permitAll())
                                .addFilterAt(new JwtTokenAuthenticationFilter(tokenProvider),
                                                SecurityWebFiltersOrder.HTTP_BASIC)
                                .build();

        }

        private Mono<AuthorizationDecision> currentUserMatchesPath(Mono<Authentication> authentication,
                        AuthorizationContext context) {
                return authentication.map(a -> context.getVariables().get("user").equals(a.getName()))
                                .map(AuthorizationDecision::new);
        }

        @Bean
        public ReactiveUserDetailsService userDetailsService(UserRepository users) {

                return username -> users.findByUsername(username)
                                .map(u -> User.withUsername(u.getUsername()).password(u.getPassword())
                                                .authorities(u.getRoles().toArray(new String[0]))
                                                .accountExpired(!u.isActive()).credentialsExpired(!u.isActive())
                                                .disabled(!u.isActive()).accountLocked(!u.isActive()).build());
        }

        @Bean
        public ReactiveAuthenticationManager reactiveAuthenticationManager(
                        ReactiveUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
                var authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
                authenticationManager.setPasswordEncoder(passwordEncoder);
                return authenticationManager;
        }

}
