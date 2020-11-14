package fr.high4technology.high4resto.bean.WebConfig;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface WebConfigRespository extends ReactiveMongoRepository<WebConfig, String> {
    
}
