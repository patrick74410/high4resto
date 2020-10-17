package fr.high4technology.high4resto.bean.ItemMenu;
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
@RequestMapping("/itemMenu")
@RequiredArgsConstructor
public class ItemMenuController {
	@Autowired
    private ItemMenuRepository itemMenus;
    
	@GetMapping("/find/")
	public Flux<ItemMenu> getAllAll()
	{
		return itemMenus.findAll();
	}

	@GetMapping("/find/{idItem}")
	public Mono<ItemMenu> getById(@PathVariable String idItem){
		return itemMenus.findById(idItem);
	}

	@DeleteMapping("/delete/{idItem}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable String idItem)
	{
	
		return itemMenus.deleteById(idItem)
                .map( r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.ok().<Void>build());
	}

	@PutMapping("/insert/")
	Mono<ItemMenu> insert(@RequestBody ItemMenu itemMenu)
	{
		return itemMenus.save(itemMenu);
	}

	@PutMapping("/update/")
	Mono<ItemMenu> update(@RequestBody ItemMenu itemMenu)
	{
		return itemMenus.findById(itemMenu.getId())
		.map(foundItem -> {
			foundItem.setName(itemMenu.getName());
            foundItem.setOrder(itemMenu.getOrder());
            foundItem.setAllergenes(itemMenu.getAllergenes());
            foundItem.setCategorie(itemMenu.getCategorie());
            foundItem.setDescription(itemMenu.getDescription());
            foundItem.setPrice(itemMenu.getPrice());
            foundItem.setSourceImage(itemMenu.getSourceImage());
			return foundItem;
		 })
		.flatMap(itemMenus::save);
	}    

}
