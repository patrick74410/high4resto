package fr.high4technology.high4resto.bean.ItemMenu;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ItemMenuRepository extends ReactiveMongoRepository<ItemMenu, String> {
    
}
