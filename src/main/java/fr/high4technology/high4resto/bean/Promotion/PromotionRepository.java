package fr.high4technology.high4resto.bean.Promotion;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PromotionRepository extends ReactiveMongoRepository<Promotion,String>{
    
}
