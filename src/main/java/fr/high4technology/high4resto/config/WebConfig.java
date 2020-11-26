package fr.high4technology.high4resto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class WebConfig {

    @Bean
    CorsWebFilter corsWebFilter() {
        var corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedMethods(List.of("PUT", "POST", "GET", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedOrigins(List.of("*"));
        corsConfiguration.setAllowedHeaders(
                List.of("Access-Control-Allow-Headers", "Access-Control-Allow-Origin", "Access-Control-Request-Method",
                        "Access-Control-Request-Headers", "Origin", "Cache-Control", "Content-Type", "Authorization"));
        corsConfiguration.setAllowCredentials(true);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsWebFilter(source);
    }

}
