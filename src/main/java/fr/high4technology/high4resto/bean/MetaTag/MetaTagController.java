package fr.high4technology.high4resto.bean.MetaTag;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.high4technology.high4resto.bean.Struct.KeyMap;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/metaTag")
@RequiredArgsConstructor

public class MetaTagController {

	@Autowired
	private MetaTagRepository metaTags;

	@GetMapping("/find/")
	public Flux<MetaTag> getAllAll() {
		return metaTags.findAll()
				.switchIfEmpty(this.metaTags.save(MetaTag.builder().author("").description("").facebookDescription("")
						.facebookImage("").facebookTitle("").keywords("").other(new ArrayList<KeyMap>())
						.twitterAuthor("").twitterDescription("").twitterImage("").twitterTitle("").build()))
				.flatMap(e -> {
					return metaTags.findAll();
				});
	}

	@GetMapping("/find/{idItem}")
	public Mono<MetaTag> getById(@PathVariable String idItem) {
		return metaTags.findById(idItem);
	}

	@DeleteMapping("/delete/{idItem}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable String idItem) {

		return metaTags.deleteById(idItem).map(r -> ResponseEntity.ok().<Void>build())
				.defaultIfEmpty(ResponseEntity.ok().<Void>build());
	}

	@PutMapping("/insert/")
	Mono<MetaTag> insert(@RequestBody MetaTag metaTag) {
		return metaTags.save(metaTag);
	}

	@PutMapping("/update/")
	Mono<MetaTag> update(@RequestBody MetaTag metaTag) {
		return metaTags.findById(metaTag.getId()).map(foundItem -> {
			foundItem.setAuthor(metaTag.getAuthor());
			foundItem.setDescription(metaTag.getDescription());
			foundItem.setFacebookDescription(metaTag.getFacebookDescription());
			foundItem.setFacebookImage(metaTag.getFacebookImage());
			foundItem.setFacebookTitle(metaTag.getFacebookTitle());
			foundItem.setKeywords(metaTag.getKeywords());
			foundItem.setOther(metaTag.getOther());
			foundItem.setTwitterAuthor(metaTag.getTwitterAuthor());
			foundItem.setTwitterDescription(metaTag.getTwitterDescription());
			foundItem.setTwitterImage(metaTag.getTwitterImage());
			foundItem.setTwitterTitle(metaTag.getTwitterTitle());
			return foundItem;
		}).flatMap(metaTags::save);
	}

}
