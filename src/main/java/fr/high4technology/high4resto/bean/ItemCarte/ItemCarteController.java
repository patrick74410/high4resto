package fr.high4technology.high4resto.bean.ItemCarte;
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
@RequestMapping("/itemCarte")
@RequiredArgsConstructor
public class ItemCarteController {
	@Autowired
    private ItemCarteRepository itemCartes;
    
	@GetMapping("/find/")
	public Flux<ItemCarte> getAllAll()
	{
		return itemCartes.findAll();
	}

	@GetMapping("/find/{idItem}")
	public Mono<ItemCarte> getById(@PathVariable String idItem){
		return itemCartes.findById(idItem);
	}

	@DeleteMapping("/delete/{idItem}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable String idItem)
	{
	
		return itemCartes.deleteById(idItem)
                .map( r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.ok().<Void>build());
	}

	@PutMapping("/insert/")
	Mono<ItemCarte> insert(@RequestBody ItemCarte itemCarte)
	{
		return itemCartes.save(itemCarte);
	}

	@PutMapping("/update/")
	Mono<ItemCarte> update(@RequestBody ItemCarte itemCarte)
	{
		return itemCartes.findById(itemCarte.getId())
		.map(foundItem -> {
			foundItem.setName(itemCarte.getName());
            foundItem.setOrder(itemCarte.getOrder());
            foundItem.setAllergenes(itemCarte.getAllergenes());
            foundItem.setCategorie(itemCarte.getCategorie());
            foundItem.setDescription(itemCarte.getDescription());
			foundItem.setPrice(itemCarte.getPrice());
			foundItem.setTva(itemCarte.getTva());
			foundItem.setSourceImage(itemCarte.getSourceImage());
			foundItem.setOptions(itemCarte.getOptions());
			foundItem.setVisible(itemCarte.isVisible());
			foundItem.setPromotions(itemCarte.getPromotions());
			foundItem.setStock(itemCarte.getStock());
			return foundItem;
		 })
		.flatMap(itemCartes::save);
	}    

}
