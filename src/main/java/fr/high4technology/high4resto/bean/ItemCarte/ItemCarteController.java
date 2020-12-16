package fr.high4technology.high4resto.bean.ItemCarte;

import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.high4technology.high4resto.bean.Stock.StockRepository;
import fr.high4technology.high4resto.bean.Stock.Stock;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/itemCarte")
@RequiredArgsConstructor
public class ItemCarteController {
	@Autowired
	private ItemCarteRepository itemCartes;
	@Autowired
	private StockRepository stocks;

	@GetMapping("/find/")
	public Flux<ItemCarte> getAllAll() {
		return itemCartes.findAll();
	}

	@GetMapping("/find/{idItem}")
	public Mono<ItemCarte> getById(@PathVariable String idItem) {

		return itemCartes.findById(idItem);
	}

	@GetMapping("/filter/{id}")
	public Flux<ItemCarte> getByFilter(@PathVariable String id) {

		return stocks.findAll().filter(stock->stock.getItem().isVisible()).filter(stock -> stock.getItem().getCategorie().getId().equals(id)).sort((a, b) -> {
			return a.getItem().getId().compareTo(b.getItem().getId());
		})
				// Je regroupe le tout et je compte le stock disponible
				.transformDeferred(source -> {
					AtomicReference<Stock> last = new AtomicReference<>(null);
					Stock stock = Stock.builder().item(ItemCarte.builder().stock(0).build()).build();
					last.set(stock);
					return source
							.windowUntil(i -> !i.getItem().getId().equals(last.getAndSet(i).getItem().getId()), true)
							.flatMap(window -> window.reduce((i1, i2) -> {
								i1.getItem().setStock(i1.getItem().getStock() + i2.getItem().getStock());
								return i1;
							}));
				}).flatMap(stock -> Flux.just(stock.getItem()))

				// Je trie en fonction de l'ordre a affiché
				.sort((itemA, itemB) -> {
					if (itemA.getOrder() > itemB.getOrder())
						return 1;
					else if (itemA.getOrder() < itemB.getOrder())
						return -1;
					else
						return 0;

				});
	}

	@DeleteMapping("/delete/{idItem}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable String idItem) {
		return itemCartes.deleteById(idItem).map(r -> ResponseEntity.ok().<Void>build())
				.defaultIfEmpty(ResponseEntity.ok().<Void>build());
	}

	@PutMapping("/insert/")
	Mono<ItemCarte> insert(@RequestBody ItemCarte itemCarte) {
		itemCarte.setStock(1);
		return itemCartes.save(itemCarte);
	}

	@PutMapping("/update/")
	Mono<ItemCarte> update(@RequestBody ItemCarte itemCarte) {
		return itemCartes.findById(itemCarte.getId()).map(foundItem -> {
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
			foundItem.setRemarque(itemCarte.getRemarque());
			;
			return foundItem;
		}).flatMap(itemCartes::save);
	}

	@GetMapping("/isAvailable/{idItem}")
	Mono<Boolean> isAvailable(@RequestBody String idItem) {
		return Mono.just(false);
	}

}
