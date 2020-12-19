package fr.high4technology.high4resto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
public class High4restoApplication {
	public static void main(String[] args) {
		SpringApplication.run(High4restoApplication.class, args);
	}
}
