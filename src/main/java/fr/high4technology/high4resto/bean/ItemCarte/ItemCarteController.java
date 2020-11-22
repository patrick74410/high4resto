package fr.high4technology.high4resto.bean.ItemCarte;
/*
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;
*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
import fr.high4technology.high4resto.bean.Stock.StockRepository;
import fr.high4technology.high4resto.bean.Stock.Stock;
import fr.high4technology.high4resto.bean.Struct.BetweenTime;
import lombok.extern.slf4j.Slf4j;
*/
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/itemCarte")
@RequiredArgsConstructor
//@Slf4j
public class ItemCarteController {
	@Autowired
	private ItemCarteRepository itemCartes;
//	@Autowired
//	private StockRepository stocks;
    
	@GetMapping("/find/")
	public Flux<ItemCarte> getAllAll()
	{
		return itemCartes.findAll();
	}

	@GetMapping("/find/{idItem}")
	public Mono<ItemCarte> getById(@PathVariable String idItem){
		
		return itemCartes.findById(idItem);
	}

	@GetMapping("/filter/{categorieId}")
	public Flux<ItemCarte> getByFilter(@PathVariable String categorieId){
		// Je prends les items visible
		return itemCartes.findAll().filter(item->item.isVisible())
		// Je ne sélectionne que les items qui font partie de la catégorie demandée
		.filter(item->item.getCategorie().getId().equals(categorieId))
		/*
		// Je vais cherché dans le stock
		.flatMap(itemCartes->{
			return stocks.findAll().filter(stock->stock.getItem().getId().equals(itemCartes.getId()));
		})
		.filter(stock->{
			// Est ce que je suis dans la bonne journée ?
			Calendar day=Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
			day.setTime(new Date());
			int dayOfWeek=day.get(Calendar.DAY_OF_WEEK);
			if(stock.getDisponibility().getJourValide()[dayOfWeek])
			return true;
			return false;
		})

		.filter(stock->{
			// Est-ce entre les date de début et de fin ?
			Date begin=new Date();
			Date end=new Date();
			try
			{
				begin=new SimpleDateFormat("dd/MM/yyyy").parse(stock.getDisponibility().getDateDebut());
				end=new SimpleDateFormat("dd/MM/yyyy").parse(stock.getDisponibility().getDateFin());
			}
			catch(Exception e)
			{
				log.error(e.getMessage());
			}
			Date now=Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris")).getTime();
			return begin.compareTo(now) * now.compareTo(end) > 0;
		})
		.filter(stock->{
			// Est-ce que je suis dans le bon crénau horaire ?
			for(BetweenTime between:stock.getDisponibility().getDisponible())
			{
				LocalTime now = LocalTime.of(Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris")).getTime().getHours(), Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris")).getTime().getMinutes());
				if(now.isAfter(LocalTime.parse(between.getDebut())))
				{
					if(now.isBefore(LocalTime.parse(between.getFin())))
					{
						return true;
					}
				}
			}
			return false;
		})
		// Je trie avec l'id des Items
		.sort((a,b)->{
			return a.getItem().getId().compareTo(b.getItem().getId());			
		})
		// Je regroupe le tout et je compte le stock disponible
		.transformDeferred(source -> {
			AtomicReference<Stock> last = new AtomicReference<>(null);
			return source
			  .windowUntil(i -> !i.getItem().getId().equals(last.getAndSet(i).getItem().getId()),true)
			  .flatMap(window->window.reduce((i1,i2)-> {
				  	i1.getItem().setStock(i1.getItem().getStock()+i2.getItem().getStock());
					return i1;
				} 
			));
		})
		// Je convertit l'élément en stock en item;
		.flatMap(stock->Flux.just(stock.getItem()))
		*/
		// Je trie en fonction de l'ordre a affiché
		.sort((itemA,itemB)->{
			if(itemA.getOrder()>itemB.getOrder())
				return 1;
			else if(itemA.getOrder()<itemB.getOrder())
				return -1;
			else
				return 0;

		});
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
		itemCarte.setStock(1);
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
	
	@GetMapping("/isAvailable/{idItem}")
	Mono<Boolean> isAvailable(@RequestBody String idItem)
	{
		return Mono.just(false);
	}

}
