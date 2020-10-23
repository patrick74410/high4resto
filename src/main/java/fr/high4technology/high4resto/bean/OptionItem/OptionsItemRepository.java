package fr.high4technology.high4resto.bean.OptionItem;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface OptionsItemRepository extends ReactiveMongoRepository<OptionsItem,String> {
    
}
