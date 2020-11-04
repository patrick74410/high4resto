package fr.high4technology.high4resto.bean.MetaTag;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MetaTagRepository extends ReactiveMongoRepository<MetaTag,String> {
    
}
