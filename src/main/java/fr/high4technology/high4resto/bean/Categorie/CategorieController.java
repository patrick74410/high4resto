package fr.high4technology.high4resto.bean.Categorie;

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
@RequestMapping("/categorie")
@RequiredArgsConstructor
public class CategorieController {
	@Autowired
	private CategorieRepository categories;

	@GetMapping("/find/")
	public Flux<Categorie> getAllAll()
	{
		return categories.findAll();
	}

	@GetMapping("/find/{idItem}")
	public Mono<Categorie> getById(@PathVariable String idItem){
		return categories.findById(idItem);
	}

	@DeleteMapping("/delete/{idItem}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable String idItem)
	{
	
		return categories.deleteById(idItem)
                .map( r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@PutMapping("/insert/")
	Mono<Categorie> insert(@RequestBody Categorie categorie)
	{
		return categories.save(categorie);
	}

	@PutMapping("/update/{idItem}")
	Mono<Categorie> update(@RequestBody Categorie categorie,@PathVariable String idItem)
	{
		return categories.findById(idItem)
		.map(foundItem -> {
			foundItem.setName(categorie.getName());
			return foundItem;
		 })
		.flatMap(categories::save);
	}
    
}
