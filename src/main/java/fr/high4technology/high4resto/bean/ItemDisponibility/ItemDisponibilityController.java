package fr.high4technology.high4resto.bean.ItemDisponibility;

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
@RequestMapping("/api/itemDisponibility")
@RequiredArgsConstructor
public class ItemDisponibilityController {
    @Autowired
    private ItemDisponibilityRepository itemDisponibilitys;

    @GetMapping("/find/")
    public Flux<ItemDisponibility> getAllAll() {
        return itemDisponibilitys.findAll();
    }

    @GetMapping("/find/{idItem}")
    public Mono<ItemDisponibility> getOne(@PathVariable String idItem) {
        return itemDisponibilitys.findById(idItem);
    }

    @DeleteMapping("/delete/{idItem}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String idItem) {

        return itemDisponibilitys.deleteById(idItem).map(r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.ok().<Void>build());
    }

    @PutMapping("/insert/")
    Mono<ItemDisponibility> insert(@RequestBody ItemDisponibility disponibility) {
        return itemDisponibilitys.save(disponibility);
    }

    @PutMapping("/update/")
    Mono<ItemDisponibility> update(@RequestBody ItemDisponibility item) {
        return itemDisponibilitys.findById(item.getId()).map(foundItem -> {
            foundItem.setDateDebut(item.getDateDebut());
            foundItem.setDateFin(item.getDateFin());
            foundItem.setAlways(item.isAlways());
            foundItem.setDisponibility(item.getDisponibility());
            return foundItem;
        }).flatMap(itemDisponibilitys::save);
    }

}
