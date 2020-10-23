package fr.high4technology.high4resto.security.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {
    private long validityInMs = 36000000; // 10h
}