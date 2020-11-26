package fr.high4technology.high4resto.bean.OptionItem;

import java.util.ArrayList;
import java.util.List;

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
@RequestMapping("/api/optionsItem")
@RequiredArgsConstructor
@Slf4j
public class OptionsItemController {
	@Autowired
	private OptionsItemRepository optionsItem;
	@Autowired
	private ItemCarteRepository items;

	@GetMapping("/find/")
	public Flux<OptionsItem> getAll() {
		return optionsItem.findAll();
	}

	@GetMapping("/find/{idItem}")
	public Mono<OptionsItem> getById(@PathVariable String idItem) {
		return optionsItem.findById(idItem);
	}

	@DeleteMapping("/delete/{idItem}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable String idItem) {
		return this.optionsItem.deleteById(idItem).and(items.findAll().map(item -> {
			List<OptionsItem> finalOptionsItem = new ArrayList<OptionsItem>();
			for (OptionsItem option : item.getOptions()) {
				if (!option.getId().equals(idItem))
					finalOptionsItem.add(option);
			}
			item.setOptions(finalOptionsItem);
			return item;
		}).flatMap(items::save)).map(r -> ResponseEntity.ok().<Void>build())
				.defaultIfEmpty(ResponseEntity.ok().<Void>build());
	}

	@PutMapping("/insert/")
	Mono<OptionsItem> insert(@RequestBody OptionsItem optionsItem) {
		return this.optionsItem.save(optionsItem);
	}

	@PutMapping("/update/")
	Mono<OptionsItem> update(@RequestBody OptionsItem optionsItem) {
		return this.optionsItem.findById(optionsItem.getId()).map(foundItem -> {
			foundItem.setUnique(optionsItem.isUnique());
			foundItem.setOptions(optionsItem.getOptions());
			foundItem.setLabel(optionsItem.getLabel());
			return foundItem;
		}).flatMap(optionItem -> {
			items.findAll().subscribe(article -> {
				int idx = 0;
				for (OptionsItem option : article.getOptions()) {
					if (option.getId().equals(optionItem.getId()))
						article.getOptions().set(idx, optionItem);
					idx += 1;
				}
				var flux = items.save(article);
				flux.doOnSubscribe(data -> log.info("data:" + data)).thenMany(flux).subscribe(
						data -> log.info("data:" + data), err -> log.error("error:" + err),
						() -> log.info("done initialization..."));

			});
			return this.optionsItem.save(optionItem);
		});
	}
}
