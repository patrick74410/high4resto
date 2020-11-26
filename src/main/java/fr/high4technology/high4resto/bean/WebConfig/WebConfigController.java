package fr.high4technology.high4resto.bean.WebConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.high4technology.high4resto.bean.Image.Image;
import fr.high4technology.high4resto.bean.ImageCategorie.ImageCategorie;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/webConfig")
@RequiredArgsConstructor
public class WebConfigController {
	@Autowired
	private WebConfigRespository configs;

	@GetMapping("/find/")
	public Flux<WebConfig> getAllAll() {
		return configs.findAll().switchIfEmpty(configs.save(WebConfig.builder().caroussel(new ImageCategorie())
				.title("").googleMapApi("").qty(true).logo(new Image()).build())).flatMap(t -> configs.findAll());
	}

	@GetMapping("/find/{idItem}")
	public Mono<WebConfig> getById(@PathVariable String idItem) {
		return configs.findById(idItem);
	}

	@DeleteMapping("/delete/{idItem}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable String idItem) {

		return configs.deleteById(idItem).map(r -> ResponseEntity.ok().<Void>build())
				.defaultIfEmpty(ResponseEntity.ok().<Void>build());
	}

	@PutMapping("/insert/")
	Mono<WebConfig> insert(@RequestBody WebConfig config) {
		return configs.save(config);
	}

	@PutMapping("/update/")
	Mono<WebConfig> update(@RequestBody WebConfig config) {
		return configs.findById(config.getId()).map(foundItem -> {
			foundItem.setLogo(config.getLogo());
			foundItem.setGoogleMapApi(config.getGoogleMapApi());
			foundItem.setTitle(config.getTitle());
			foundItem.setCaroussel(config.getCaroussel());
			foundItem.setQty(config.isQty());
			foundItem.setAuth0Domain(config.getAuth0Domain());
			foundItem.setAuth0Key(config.getAuth0Key());
			return foundItem;
		}).flatMap(item -> {
			return configs.save(item);
		});
	}

}
