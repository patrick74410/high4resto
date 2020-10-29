package fr.high4technology.high4resto.bean.Promotion;

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
@RequestMapping("/promotions")
@RequiredArgsConstructor
public class PromotionController {
    @Autowired
    private PromotionRepository promotionsR;
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
        return promotionsR.deleteById(idPromotion)
        .map(r-> ResponseEntity.ok().<Void>build())
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
        .flatMap(this.promotionsR::save);
    }
}
