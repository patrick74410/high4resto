package fr.high4technology.high4resto.bean.ItemPreparation;

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
@RequestMapping("/api/itemPreparation")
@RequiredArgsConstructor

public class ItemPreparationController {
	@Autowired
	private ItemPreparationRepository itemsPreparationLinks;

	@GetMapping("/find/")
	public Flux<ItemPreparation> getAll() {
		return itemsPreparationLinks.findAll();
	}

	@GetMapping("/find/{idItem}")
	public Mono<ItemPreparation> getById(@PathVariable String idItem) {
		return itemsPreparationLinks.findById(idItem);
	}

	@DeleteMapping("/delete/{idItem}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable String idItem) {

		return itemsPreparationLinks.deleteById(idItem).map(r -> ResponseEntity.ok().<Void>build())
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@PutMapping("/insert/")
	Mono<ItemPreparation> insert(@RequestBody ItemPreparation ingredient) {
		return itemsPreparationLinks.save(ingredient);
	}

	@PutMapping("/update/")
	Mono<ItemPreparation> update(@RequestBody ItemPreparation item) {
		return itemsPreparationLinks.findById(item.getId()).map(foundItem -> {
			foundItem.setRoleName(item.getRoleName());
			foundItem.setPart(item.getPart());
			foundItem.setName(item.getName());
			foundItem.setTime(item.getTime());
			return foundItem;
		}).flatMap(itemsPreparationLinks::save);
	}
}
