package fr.high4technology.high4resto.bean.ItemRoleLink;

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
@RequestMapping("/api/itemRoleLink")
@RequiredArgsConstructor

public class ItemRoleLinkController {
    @Autowired
    private ItemRoleLinkRepository itemsRoleLinks;
	@GetMapping("/find/")
	public Flux<ItemRoleLink> getAll() {
		return itemsRoleLinks.findAll();
	}

	@GetMapping("/find/{idItem}")
	public Mono<ItemRoleLink> getById(@PathVariable String idItem) {
		return itemsRoleLinks.findById(idItem);
	}

	@DeleteMapping("/delete/{idItem}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable String idItem) {

		return itemsRoleLinks.deleteById(idItem).map(r -> ResponseEntity.ok().<Void>build())
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@PutMapping("/insert/")
	Mono<ItemRoleLink> insert(@RequestBody ItemRoleLink ingredient) {
		return itemsRoleLinks.save(ingredient);
	}

	@PutMapping("/update/{idItem}")
	Mono<ItemRoleLink> update(@RequestBody ItemRoleLink item, @PathVariable String idItem) {
		return itemsRoleLinks.findById(idItem).map(foundItem -> {
            foundItem.setRoleName(item.getRoleName());
            foundItem.setPart(item.getPart());
			return foundItem;
		}).flatMap(itemsRoleLinks::save);
	}
}
