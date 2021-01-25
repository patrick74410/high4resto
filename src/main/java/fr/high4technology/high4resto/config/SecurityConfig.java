package fr.high4technology.high4resto.config;

import fr.high4technology.high4resto.Util.Variable;
import fr.high4technology.high4resto.bean.user.UserRepository;
import fr.high4technology.high4resto.security.jwt.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
public class SecurityConfig {

        @Bean
        SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http, JwtTokenProvider tokenProvider,
                        ReactiveAuthenticationManager reactiveAuthenticationManager) {
                return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                                .authenticationManager(reactiveAuthenticationManager)
                                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                                .authorizeExchange(it -> it.pathMatchers(HttpMethod.OPTIONS).permitAll()
                                                .pathMatchers("/"+Variable.apiPath+"/config-user/**").hasRole("ADMIN")
                                                .pathMatchers("/"+Variable.apiPath+"/config-users/**").hasRole("ADMIN")
                                                .pathMatchers("/"+Variable.apiPath+"/allergene/find/**").permitAll()
                                                .pathMatchers("/"+Variable.apiPath+"/allergene/delete/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/allergene/insert/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/allergene/update/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/categorie/find/**").permitAll()
                                                .pathMatchers("/"+Variable.apiPath+"/categorie/delete/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/categorie/insert/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/categorie/update/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/images/find/**").permitAll()
                                                .pathMatchers("/"+Variable.apiPath+"/images/delete/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/images/insert/**").hasRole("EDITOR")
                                                .pathMatchers("/"+Variable.apiPath+"/images/update/**").hasRole("EDITOR")
                                                .pathMatchers("/"+Variable.apiPath+"/images/upload/**").hasRole("EDITOR")
                                                .pathMatchers("/"+Variable.apiPath+"/images/download/**").permitAll()
                                                .pathMatchers("/"+Variable.apiPath+"/ingredient/find/**").permitAll()
                                                .pathMatchers("/"+Variable.apiPath+"/ingredient/delete/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/ingredient/insert/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/ingredient/update/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/itemCarte/find/**").permitAll()
                                                .pathMatchers("/"+Variable.apiPath+"/itemCarte/delete/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/itemCarte/insert/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/itemCarte/update/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/tva/find/**").permitAll()
                                                .pathMatchers("/"+Variable.apiPath+"/tva/delete/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/tva/insert/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/tva/update/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/optionsItem/find/**").permitAll()
                                                .pathMatchers("/"+Variable.apiPath+"/optionsItem/delete/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/optionsItem/insert/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/optionsItem/update/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/promotion/find/**").permitAll()
                                                .pathMatchers("/"+Variable.apiPath+"/promotion/delete/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/promotion/insert/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/promotion/update/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/horaire/find/**").permitAll()
                                                .pathMatchers("/"+Variable.apiPath+"/horaire/delete/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/horaire/insert/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/horaire/update/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/identite/find/**").permitAll()
                                                .pathMatchers("/"+Variable.apiPath+"/identite/delete/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/identite/insert/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/identite/update/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/articleCategorie/find/**").permitAll()
                                                .pathMatchers("/"+Variable.apiPath+"/articleCategorie/delete/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/articleCategorie/insert/**").hasRole("EDITOR")
                                                .pathMatchers("/"+Variable.apiPath+"/articleCategorie/update/**").hasRole("EDITOR")
                                                .pathMatchers("/"+Variable.apiPath+"/imageCategorie/find/**").permitAll()
                                                .pathMatchers("/"+Variable.apiPath+"/imageCategorie/delete/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/imageCategorie/insert/**").hasRole("EDITOR")
                                                .pathMatchers("/"+Variable.apiPath+"/imageCategorie/update/**").hasRole("EDITOR")
                                                .pathMatchers("/"+Variable.apiPath+"/webConfig/find/**").permitAll()
                                                .pathMatchers("/"+Variable.apiPath+"/webConfig/delete/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/webConfig/insert/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/webConfig/update/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/stock/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"/itemDisponibility/**").hasRole("MANAGER")
                                                .pathMatchers("/"+Variable.apiPath+"//api/itemPreparation/**").hasRole("MANAGER")
                                                .anyExchange().permitAll())
                                .addFilterAt(new JwtTokenAuthenticationFilter(tokenProvider),
                                                SecurityWebFiltersOrder.HTTP_BASIC)
                                .build();

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
