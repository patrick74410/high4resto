package fr.high4technology.high4resto.bean.ItemDisponibility;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ItemDisponibilityRepository extends ReactiveMongoRepository<ItemDisponibility,String> {
    
}
