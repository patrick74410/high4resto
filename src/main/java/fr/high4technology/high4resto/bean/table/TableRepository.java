package fr.high4technology.high4resto.bean.table;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TableRepository extends ReactiveMongoRepository<Table, String> {

}
