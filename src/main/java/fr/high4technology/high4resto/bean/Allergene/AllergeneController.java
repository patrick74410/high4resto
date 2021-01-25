package fr.high4technology.high4resto.bean.Allergene;

import java.util.ArrayList;
import java.util.List;

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

import fr.high4technology.high4resto.WebSocket.ServerCanalHandler;
import fr.high4technology.high4resto.bean.ItemCarte.ItemCarteRepository;
import lombok.extern.slf4j.Slf4j;
import fr.high4technology.high4resto.Util.Variable;

@RestController
@RequestMapping("/"+Variable.apiPath+"/allergene")
@RequiredArgsConstructor
@Slf4j
public class AllergeneController {

	@Autowired
	private AllergenRepository allergenes;
	@Autowired
	private ItemCarteRepository items;
	@Autowired
	private ServerCanalHandler socket;

	@GetMapping("/find/")
	public Flux<Allergene> getAllAll() {
		socket.sendMessage("mon message");
		return allergenes.findAll();
	}

	@GetMapping("/find/{idItem}")
	public Mono<Allergene> getById(@PathVariable String idItem) {
		return allergenes.findById(idItem);
	}

	@DeleteMapping("/delete/{idItem}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable String idItem) {

		return allergenes.deleteById(idItem).and(items.findAll().map(item -> {
			List<Allergene> finalAllergene = new ArrayList<Allergene>();
			for (Allergene allergene : item.getAllergenes()) {
				if (!allergene.getId().equals(idItem))
					finalAllergene.add(allergene);
			}
			item.setAllergenes(finalAllergene);
			return item;
		}).flatMap(items::save)).map(r -> ResponseEntity.ok().<Void>build())
				.defaultIfEmpty(ResponseEntity.ok().<Void>build());
	}

	@PutMapping("/insert/")
	Mono<Allergene> insert(@RequestBody Allergene allergene) {
		return allergenes.save(allergene);
	}

	@PutMapping("/update/")
	Mono<Allergene> update(@RequestBody Allergene allergene) {
		return allergenes.findById(allergene.getId()).map(foundItem -> {
			foundItem.setName(allergene.getName());
			return foundItem;
		}).flatMap(allergentItem -> {
			items.findAll().subscribe(article -> {
				int idx = 0;
				for (Allergene aller : article.getAllergenes()) {
					if (aller.getId().equals(allergene.getId()))
						article.getAllergenes().set(idx, allergene);
					idx += 1;
				}
				var flux = items.save(article);
				flux.doOnSubscribe(data -> log.info("data:" + data)).thenMany(flux).subscribe(
						data -> log.info("data:" + data), err -> log.error("error:" + err),
						() -> log.info("done initialization..."));

			});
			return allergenes.save(allergentItem);
		});
	}

}