package fr.high4technology.high4resto.bean.Categorie;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CategorieRepository extends ReactiveMongoRepository<Categorie, String>{
    
}
