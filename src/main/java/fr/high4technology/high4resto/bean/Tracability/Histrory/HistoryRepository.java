package fr.high4technology.high4resto.bean.Tracability.Histrory;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface HistoryRepository extends ReactiveMongoRepository<History, String> {

}
