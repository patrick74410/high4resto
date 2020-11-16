package fr.high4technology.high4resto.bean.Promotion;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.high4technology.high4resto.bean.ItemCarte.ItemCarteRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
@Slf4j
public class PromotionController {
    @Autowired
    private PromotionRepository promotionsR;
    @Autowired
    private ItemCarteRepository items;
    
    @GetMapping("/find/")
    public Flux<Promotion> getAll()
    {
        return promotionsR.findAll();
    }

	@GetMapping("/find/{idItem}")
	public Mono<Promotion> getById(@PathVariable String idItem){
		return promotionsR.findById(idItem);
    }
    
    @DeleteMapping("/delete/{idPromotion}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String idPromotion)
    {
		return promotionsR.deleteById(idPromotion).and(
			items.findAll().map(item->{
                List<Promotion> finalPromotion=new ArrayList<Promotion>();
				for(Promotion promotion:item.getPromotions())
				{
					if(!promotion.getId().equals(idPromotion))
						finalPromotion.add(promotion);
                }
                item.setPromotions(finalPromotion);
				return item;
			}).flatMap(items::save)).map(r -> ResponseEntity.ok().<Void>build())
			.defaultIfEmpty(ResponseEntity.ok().<Void>build());
    }

    @PutMapping("/insert/")
    Mono<Promotion> insert(@RequestBody Promotion promotion)
    {
        return promotionsR.save(promotion);
    }

    @PutMapping("/update/")
    Mono<Promotion> update(@RequestBody Promotion promotion)
    {
        return this.promotionsR.findById(promotion.getId())
        .map(foundPromotion ->{
            foundPromotion.setDateDebut(promotion.getDateDebut());
            foundPromotion.setDateFin(promotion.getDateFin());
            foundPromotion.setHeureDebut(promotion.getHeureDebut());
            foundPromotion.setHeureFin(promotion.getHeureFin());
            foundPromotion.setJourFerie(promotion.isJourFerie());
            foundPromotion.setJourValide(promotion.getJourValide());
            foundPromotion.setName(promotion.getName());
            foundPromotion.setReduction(promotion.getReduction());
            foundPromotion.setPourcentage(promotion.isPourcentage());
            return foundPromotion;
        })
        .flatMap(promotionItem -> {
			items.findAll().subscribe(article -> {
				int idx = 0;
				for (Promotion promot : article.getPromotions()) {
					if (promot.getId().equals(promotion.getId()))
						article.getPromotions().set(idx, promotion);
					idx += 1;
				}
				var flux = items.save(article);
				flux.doOnSubscribe(data -> log.info("data:" + data)).thenMany(flux).subscribe(
						data -> log.info("data:" + data), err -> log.error("error:" + err),
						() -> log.info("done initialization..."));

			});
			return promotionsR.save(promotionItem);
		});
    }
}
