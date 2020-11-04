package fr.high4technology.high4resto.bean.Config;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ConfigRespository extends ReactiveMongoRepository<Config, String> {
    
}
