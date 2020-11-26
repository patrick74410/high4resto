package fr.high4technology.high4resto.bean.Allergene;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface AllergenRepository extends ReactiveMongoRepository<Allergene, String> {
}
