package fr.high4technology.high4resto.bean.Ingredient;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface IngredientRepository extends ReactiveMongoRepository<Ingredient, String> {
    
}
