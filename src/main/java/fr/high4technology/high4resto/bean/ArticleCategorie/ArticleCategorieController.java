package fr.high4technology.high4resto.bean.ArticleCategorie;
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
@RequestMapping("/api/articleCategorie")
@RequiredArgsConstructor

public class ArticleCategorieController {
	@Autowired
	private ArticleCategorieRepository articleCategories;

	@GetMapping("/find/")
	public Flux<ArticleCategorie> getAllAll()
	{
		return articleCategories.findAll();
	}

	@GetMapping("/find/{idItem}")
	public Mono<ArticleCategorie> getById(@PathVariable String idItem){
		return articleCategories.findById(idItem);
	}

	@DeleteMapping("/delete/{idItem}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable String idItem)
	{
	
		return articleCategories.deleteById(idItem)
                .map( r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.ok().<Void>build());
	}

	@PutMapping("/insert/")
	Mono<ArticleCategorie> insert(@RequestBody ArticleCategorie articleCategorie)
	{
		return articleCategories.save(articleCategorie);
	}

	@PutMapping("/update/")
	Mono<ArticleCategorie> update(@RequestBody ArticleCategorie articleCategorie)
	{
		return articleCategories.findById(articleCategorie.getId())
		.map(foundItem -> {
			foundItem.setName(articleCategorie.getName());
			foundItem.setOrder(articleCategorie.getOrder());
			foundItem.setDescription(articleCategorie.getDescription());
			foundItem.setIconImage(articleCategorie.getIconImage());
			foundItem.setImage(articleCategorie.getImage());
			foundItem.setVisible(articleCategorie.isVisible());
			return foundItem;
		 })
		.flatMap(articleCategories::save);
	}
    
}
