package fr.high4technology.high4resto.bean.Allergene;

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


@RestController
@RequestMapping("/allergene")
@RequiredArgsConstructor
public class AllergeneController {

	@Autowired
	private AllergenRepository allergenes;

	@GetMapping("/find/")
	public Flux<Allergene> getAllAll()
	{
		return allergenes.findAll();
	}

	@GetMapping("/find/{idItem}")
	public Mono<Allergene> getById(@PathVariable String idItem){
		return allergenes.findById(idItem);
	}

	@DeleteMapping("/delete/{idItem}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable String idItem)
	{
	
		return allergenes.deleteById(idItem)
                .map( r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@PutMapping("/insert/")
	Mono<Allergene> insert(@RequestBody Allergene allergene)
	{
		return allergenes.save(allergene);
	}

	@PutMapping("/update/{idItem}")
	Mono<Allergene> update(@RequestBody Allergene allergene,@PathVariable String idItem)
	{
		return allergenes.findById(idItem)
		.map(foundItem -> {
			foundItem.setName(allergene.getName());
			return foundItem;
		 })
		.flatMap(allergenes::save);
	}

}