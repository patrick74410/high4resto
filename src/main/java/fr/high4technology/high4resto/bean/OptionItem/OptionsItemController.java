package fr.high4technology.high4resto.bean.OptionItem;

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
@RequestMapping("/optionsItem")
@RequiredArgsConstructor
public class OptionsItemController {
    @Autowired
    private OptionsItemRepository optionsItem;
	@GetMapping("/find/")
	public Flux<OptionsItem> getAllAll()
	{
		return optionsItem.findAll();
	}

	@GetMapping("/find/{idItem}")
	public Mono<OptionsItem> getById(@PathVariable String idItem){
		return optionsItem.findById(idItem);
	}

	@DeleteMapping("/delete/{idItem}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable String idItem)
	{
	
		return optionsItem.deleteById(idItem)
                .map( r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.ok().<Void>build());
	}

	@PutMapping("/insert/")
	Mono<OptionsItem> insert(@RequestBody OptionsItem optionsItem)
	{
		return this.optionsItem.save(optionsItem);
	}

	@PutMapping("/update/")
	Mono<OptionsItem> update(@RequestBody OptionsItem optionsItem)
	{
		return this.optionsItem.findById(optionsItem.getId())
		.map(foundItem -> {
            foundItem.setUnique(optionsItem.isUnique());
			foundItem.setOptions(optionsItem.getOptions());
			foundItem.setLabel(optionsItem.getLabel());
			return foundItem;
		 })
		.flatMap(this.optionsItem::save);
	}    
}
