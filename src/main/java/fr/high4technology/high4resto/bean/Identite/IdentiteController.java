package fr.high4technology.high4resto.bean.Identite;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.high4technology.high4resto.bean.Struct.KeyMap;
import fr.high4technology.high4resto.bean.Struct.Gps;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/identite")
@RequiredArgsConstructor
public class IdentiteController {
	@Autowired
	private IdentiteRepository identites;

	@GetMapping("/find/")
	public Flux<Identite> getAllAll() {
		return identites.findAll()
				.switchIfEmpty(identites
						.save(Identite.builder().adresse("").city("").complement("").contact(new ArrayList<KeyMap>())
								.coordonnee(Gps.builder().latitude(0).longitude(0).build()).nomEtablissement("").number("").siret("").zip("").build()))
				.flatMap(t -> identites.findAll());
	}

	@GetMapping("/find/{idItem}")
	public Mono<Identite> getById(@PathVariable String idItem) {
		return identites.findById(idItem);
	}

	@DeleteMapping("/delete/{idItem}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable String idItem) {

		return identites.deleteById(idItem).map(r -> ResponseEntity.ok().<Void>build())
				.defaultIfEmpty(ResponseEntity.ok().<Void>build());
	}

	@PutMapping("/insert/")
	Mono<Identite> insert(@RequestBody Identite identite) {
		return identites.save(identite);
	}

	@PutMapping("/update/")
	Mono<Identite> update(@RequestBody Identite identite) {
		return identites.findById(identite.getId()).map(foundItem -> {
			foundItem.setAdresse(identite.getAdresse());
			foundItem.setCity(identite.getCity());
			foundItem.setComplement(identite.getComplement());
			foundItem.setContact(identite.getContact());
			foundItem.setCoordonnee(identite.getCoordonnee());
			foundItem.setNomEtablissement(identite.getNomEtablissement());
			foundItem.setNumber(identite.getNumber());
			foundItem.setSiret(identite.getSiret());
			foundItem.setZip(identite.getZip());
			foundItem.setLogo(identite.getLogo());
			foundItem.setDescription(identite.getDescription());
			return foundItem;
		}).flatMap(identites::save);
	}
}
