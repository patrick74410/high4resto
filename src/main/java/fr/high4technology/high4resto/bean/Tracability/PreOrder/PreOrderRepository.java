package fr.high4technology.high4resto.bean.Tracability.PreOrder;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PreOrderRepository extends ReactiveMongoRepository<PreOrder, String> {

}
