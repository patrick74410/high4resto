package fr.high4technology.high4resto.bean.Ingredient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ingredient")
@RequiredArgsConstructor
public class IngredientController {
	@Autowired
	private IngredientRepository ingredients;

	@GetMapping("/find/")
	public Flux<Ingredient> getAll() {
		return ingredients.findAll();
	}

	@GetMapping("/find/{idItem}")
	public Mono<Ingredient> getById(@PathVariable String idItem) {
		return ingredients.findById(idItem);
	}

	@DeleteMapping("/delete/{idItem}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable String idItem) {

		return ingredients.deleteById(idItem).map(r -> ResponseEntity.ok().<Void>build())
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@PutMapping("/insert/")
	Mono<Ingredient> insert(@RequestBody Ingredient ingredient) {
		return ingredients.save(ingredient);
	}

	@PutMapping("/update/{idItem}")
	Mono<Ingredient> update(@RequestBody Ingredient ingredient, @PathVariable String idItem) {
		return ingredients.findById(idItem).map(foundItem -> {
			foundItem.setName(ingredient.getName());
			return foundItem;
		}).flatMap(ingredients::save);
	}
}
