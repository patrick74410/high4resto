package fr.high4technology.high4resto.bean.Article;
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
@RequestMapping("/article")
@RequiredArgsConstructor

public class ArticleController {
	@Autowired
	private ArticleRepository articles;

	@GetMapping("/find/")
	public Flux<Article> getAllAll()
	{
		return articles.findAll();
	}

	@GetMapping("/find/{idItem}")
	public Mono<Article> getById(@PathVariable String idItem){
		return articles.findById(idItem);
	}

	@DeleteMapping("/delete/{idItem}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable String idItem)
	{
	
		return articles.deleteById(idItem)
                .map( r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.ok().<Void>build());
	}

	@PutMapping("/insert/")
	Mono<Article> insert(@RequestBody Article article)
	{
		return articles.save(article);
	}

	@PutMapping("/update/")
	Mono<Article> update(@RequestBody Article article)
	{
		return articles.findById(article.getId())
		.map(foundItem -> {
            foundItem.setCategorie(article.getCategorie());
            foundItem.setImage(article.getImage());
            foundItem.setOnTop(article.isOnTop());
            foundItem.setVisible(article.isVisible());
            foundItem.setTitle(article.getTitle());
            foundItem.setResume(article.getResume());
            foundItem.setContent(article.getContent());
            foundItem.setDate(article.getDate());
            foundItem.setAuthor(article.getAuthor());
			return foundItem;
		 })
		.flatMap(articles::save);
	}
       
}