package fr.high4technology.high4resto.bean.Album;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/album")
@RequiredArgsConstructor

public class AlbumController {

    @Autowired
    private AlbumRepository albums;
 
	@GetMapping("/find/")
	public Flux<Album> getAllAll()
	{
		return albums.findAll();
	}

	@GetMapping("/find/{idItem}")
	public Mono<Album> getById(@PathVariable String idItem){
		return albums.findById(idItem);
	}

	@DeleteMapping("/delete/{idItem}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable String idItem)
	{
	
		return albums.deleteById(idItem)
				.map( r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.ok().<Void>build());
	}

	@PutMapping("/insert/")
	Mono<Album> insert(@RequestBody Album album)
	{
		return albums.save(album);
	}

	@PutMapping("/update/")
	Mono<Album> update(@RequestBody Album album)
	{
		return albums.findById(album.getId())
		.map(foundItem -> {
            foundItem.setName(album.getName());
			foundItem.setPhotos(album.getPhotos());
			foundItem.setDescription(album.getDescription());
			foundItem.setVisible(album.isVisible());
			return foundItem;
		 })
		.flatMap(albums::save);
	}
    
    
}
