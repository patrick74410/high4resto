package fr.high4technology.high4resto.bean.Stock;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface StockRepository extends ReactiveMongoRepository<Stock,String>{
    
}
