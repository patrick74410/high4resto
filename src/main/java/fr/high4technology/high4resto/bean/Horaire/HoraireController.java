package fr.high4technology.high4resto.bean.Horaire;

import java.util.ArrayList;

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
import fr.high4technology.high4resto.Util.Variable;
import fr.high4technology.high4resto.bean.Struct.BetweenTime;

@RestController
@RequestMapping("/"+Variable.apiPath+"/horaire")
@RequiredArgsConstructor
public class HoraireController {

	@Autowired
	private HoraireRepository horaires;

	@GetMapping("/find/")
	public Flux<Horaire> getAllAll() {
		return horaires.findAll()
				.switchIfEmpty(horaires
						.save(Horaire.builder().lundi(new ArrayList<BetweenTime>()).mardi(new ArrayList<BetweenTime>())
								.mercredi(new ArrayList<BetweenTime>()).jeudi(new ArrayList<BetweenTime>())
								.vendredi(new ArrayList<BetweenTime>()).samedi(new ArrayList<BetweenTime>())
								.dimanche(new ArrayList<BetweenTime>()).ferie(new ArrayList<BetweenTime>()).build()))
				.flatMap(t -> horaires.findAll());
	}

	@GetMapping("/find/{idItem}")
	public Mono<Horaire> getById(@PathVariable String idItem) {
		return horaires.findById(idItem);
	}

	@DeleteMapping("/delete/{idItem}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable String idItem) {

		return horaires.deleteById(idItem).map(r -> ResponseEntity.ok().<Void>build())
				.defaultIfEmpty(ResponseEntity.ok().<Void>build());
	}

	@PutMapping("/insert/")
	Mono<Horaire> insert(@RequestBody Horaire horaire) {
		return horaires.save(horaire);
	}

	@PutMapping("/update/")
	Mono<Horaire> update(@RequestBody Horaire horaire) {
		return horaires.findById(horaire.getId()).map(foundItem -> {
			foundItem.setFerie(horaire.getFerie());
			foundItem.setLundi(horaire.getLundi());
			foundItem.setMardi(horaire.getMardi());
			foundItem.setMercredi(horaire.getMercredi());
			foundItem.setJeudi(horaire.getJeudi());
			foundItem.setVendredi(horaire.getVendredi());
			foundItem.setSamedi(horaire.getSamedi());
			foundItem.setDimanche(horaire.getDimanche());
			return foundItem;
		}).flatMap(horaires::save);
	}

}
