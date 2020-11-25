package fr.high4technology.high4resto.bean.Tracability.Trash;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TrashRepository extends ReactiveMongoRepository<Trash, String> {
    
}
