package fr.high4technology.high4resto.bean.Identite;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface IdentiteRepository extends ReactiveMongoRepository<Identite,String> {
    
}
