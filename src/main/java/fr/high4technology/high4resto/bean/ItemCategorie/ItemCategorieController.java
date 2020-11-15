package fr.high4technology.high4resto.bean.ItemCategorie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.high4technology.high4resto.bean.ItemCarte.ItemCarteRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/categorie")
@RequiredArgsConstructor
@Slf4j
public class ItemCategorieController {
	@Autowired
	private ItemCategorieRepository itemCategories;
	@Autowired
	private ItemCarteRepository items;

	@GetMapping("/find/")
	public Flux<ItemCategorie> getAllAll()
	{
		return itemCategories.findAll();
	}

	@GetMapping("/find/{idItem}")
	public Mono<ItemCategorie> getById(@PathVariable String idItem){
		return itemCategories.findById(idItem);
	}

	@DeleteMapping("/delete/{idItem}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable String idItem)
	{
		return itemCategories.deleteById(idItem).and(items.findAll().map(item -> {
			if (item.getCategorie().getId().equals(idItem))
				item.setCategorie(null);
				return item;
		}).flatMap(items::save)).map(r -> ResponseEntity.ok().<Void>build())
				.defaultIfEmpty(ResponseEntity.ok().<Void>build());
	}

	@PutMapping("/insert/")
	Mono<ItemCategorie> insert(@RequestBody ItemCategorie categorie)
	{
		return itemCategories.save(categorie);
	}

	@PutMapping("/update/")
	Mono<ItemCategorie> update(@RequestBody ItemCategorie categorie)
	{
		return itemCategories.findById(categorie.getId())
		.map(foundItem -> {
			foundItem.setName(categorie.getName());
			foundItem.setOrder(categorie.getOrder());
			foundItem.setDescription(categorie.getDescription());
			foundItem.setIconImage(categorie.getIconImage());
			foundItem.setImage(categorie.getImage());
			return foundItem;
		 })
		.flatMap(categorieItem -> {
			items.findAll().subscribe(article -> {
				if (article.getCategorie().getId().equals(categorieItem.getId())) {
					article.setCategorie(categorieItem);
					var flux = items.save(article);
					flux.doOnSubscribe(data -> log.info("data:" + data)).thenMany(flux).subscribe(
							data -> log.info("data:" + data), err -> log.error("error:" + err),
							() -> log.info("done initialization..."));

				}
			});
			return itemCategories.save(categorieItem);
		});
	}
    
}
