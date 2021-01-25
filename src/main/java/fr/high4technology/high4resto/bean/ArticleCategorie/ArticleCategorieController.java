package fr.high4technology.high4resto.bean.ArticleCategorie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.high4technology.high4resto.Util.Util;
import fr.high4technology.high4resto.Util.Variable;
import fr.high4technology.high4resto.bean.Article.ArticleRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/"+Variable.apiPath+"/articleCategorie")
@RequiredArgsConstructor
@Slf4j
public class ArticleCategorieController {
	@Autowired
	private ArticleCategorieRepository articleCategories;
	@Autowired
	private ArticleRepository articles;

	@GetMapping("/find/")
	public Flux<ArticleCategorie> getAllAll() {
		return articleCategories.findAll().sort((a,b)->{
			if(a.getOrder()>b.getOrder())
				return 1;
			else if(a.getOrder()<b.getOrder())
				return -1;
			else
				return 0;
		});
	}

	@GetMapping("/find/{idItem}")
	public Mono<ArticleCategorie> getById(@PathVariable String idItem) {
		return articleCategories.findById(idItem);
	}

	@DeleteMapping("/delete/{idItem}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable String idItem) {
		return articleCategories.deleteById(idItem).and(articles.findAll().map(article -> {
			if (article.getCategorie().getId().equals(idItem))
				article.setCategorie(null);
			return article;
		}).flatMap(articles::save)).map(r -> ResponseEntity.ok().<Void>build())
				.defaultIfEmpty(ResponseEntity.ok().<Void>build());
	}

	@PutMapping("/insert/")
	Mono<ArticleCategorie> insert(@RequestBody ArticleCategorie articleCategorie) {
		articleCategorie.setId(Util.encodeValue(articleCategorie.getName()));
		return articleCategories.save(articleCategorie);
	}

	@PutMapping("/update/")
	Mono<ArticleCategorie> update(@RequestBody ArticleCategorie articleCategorie) {
		return articleCategories.findById(articleCategorie.getId()).map(foundItem -> {
			foundItem.setName(articleCategorie.getName());
			foundItem.setOrder(articleCategorie.getOrder());
			foundItem.setDescription(articleCategorie.getDescription());
			foundItem.setIconImage(articleCategorie.getIconImage());
			foundItem.setImage(articleCategorie.getImage());
			foundItem.setVisible(articleCategorie.isVisible());
			return foundItem;
		}).flatMap(categorie -> {
			articles.findAll().subscribe(article -> {
				if (article.getCategorie().getId().equals(categorie.getId())) {
					article.setCategorie(categorie);
					var flux = articles.save(article);
					flux.doOnSubscribe(data -> log.info("data:" + data)).thenMany(flux).subscribe(
							data -> log.info("data:" + data), err -> log.error("error:" + err),
							() -> log.info("done initialization..."));

				}
			});
			return articleCategories.save(categorie);
		});
	}

}
