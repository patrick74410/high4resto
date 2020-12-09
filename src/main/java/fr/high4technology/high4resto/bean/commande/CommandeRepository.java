package fr.high4technology.high4resto.bean.commande;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CommandeRepository extends ReactiveMongoRepository<Commande, String> {

}
