package fr.high4technology.high4resto.bean.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.high4technology.high4resto.bean.Album.Album;
import fr.high4technology.high4resto.bean.Image.Image;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class ConfigController {
    @Autowired
    private ConfigRespository configs;
    @GetMapping("/find/")
	public Flux<Config> getAllAll() {
		return configs.findAll()
				.switchIfEmpty(configs
                        .save(Config.builder().banniere(new Album()).title("").description("").
                        logo(new Image()).mapApiKey("").payementApiKey("").build()))
				.flatMap(t -> configs.findAll());
	}

	@GetMapping("/find/{idItem}")
	public Mono<Config> getById(@PathVariable String idItem) {
		return configs.findById(idItem);
	}

	@DeleteMapping("/delete/{idItem}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable String idItem) {

		return configs.deleteById(idItem).map(r -> ResponseEntity.ok().<Void>build())
				.defaultIfEmpty(ResponseEntity.ok().<Void>build());
	}

	@PutMapping("/insert/")
	Mono<Config> insert(@RequestBody Config config) {
		return configs.save(config);
	}

	@PutMapping("/update/")
	Mono<Config> update(@RequestBody Config config) {
		return configs.findById(config.getId()).map(foundItem -> {
            foundItem.setDescription(config.getDescription());
            foundItem.setLogo(config.getLogo());
            foundItem.setTitle(config.getTitle());
            foundItem.setMapApiKey(config.getMapApiKey());
            foundItem.setPayementApiKey(config.getMapApiKey());
            foundItem.setBanniere(config.getBanniere());
			return foundItem;
		}).flatMap(configs::save);
	}
   
    
}
