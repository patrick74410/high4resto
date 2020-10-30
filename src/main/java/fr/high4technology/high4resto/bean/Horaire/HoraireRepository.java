package fr.high4technology.high4resto.bean.Horaire;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface HoraireRepository  extends ReactiveMongoRepository<Horaire,String>{
    
}
