package fr.high4technology.high4resto.bean.ItemCarte;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ItemCarteRepository extends ReactiveMongoRepository<ItemCarte, String> {
    
}
