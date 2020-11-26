package fr.high4technology.high4resto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;

import fr.high4technology.high4resto.domain.PersistentEntityCallback;

@Configuration
public class MongoConfig {

    @Bean
    public PersistentEntityCallback persistentEntityCallback() {
        return new PersistentEntityCallback();
    }

    @Bean
    public ReactiveGridFsTemplate reactiveGridFsTemplate(ReactiveMongoDatabaseFactory reactiveMongoDbFactory,
            MappingMongoConverter mappingMongoConverter) {
        return new ReactiveGridFsTemplate(reactiveMongoDbFactory, mappingMongoConverter);
    }

}