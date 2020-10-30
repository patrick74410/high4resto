package fr.high4technology.high4resto.bean.Album;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface AlbumRepository extends ReactiveMongoRepository<Album,String>{
    
}
