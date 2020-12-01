package fr.high4technology.high4resto.bean.Tracability.Order;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface OrderRepository extends ReactiveMongoRepository<Order, String> {

}
