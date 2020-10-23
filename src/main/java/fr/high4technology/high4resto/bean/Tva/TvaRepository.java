package fr.high4technology.high4resto.bean.Tva;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TvaRepository extends ReactiveMongoRepository<Tva, String> {
    
}
