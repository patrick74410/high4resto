package fr.high4technology.high4resto.bean.Image;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ImageRepository extends ReactiveMongoRepository<Image, String> {

}
