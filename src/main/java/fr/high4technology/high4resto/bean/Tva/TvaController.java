package fr.high4technology.high4resto.bean.Tva;

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
@RequestMapping("/api/tva")
@RequiredArgsConstructor
public class TvaController {
    @Autowired
    private TvaRepository tvas;

    @GetMapping("/find/")
    public Flux<Tva> getAllAll() {
        return tvas.findAll();
    }

    @GetMapping("/find/{idItem}")
    public Mono<Tva> getById(@PathVariable String idItem) {
        return tvas.findById(idItem);
    }

    @DeleteMapping("/delete/{idItem}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String idItem) {

        return tvas.deleteById(idItem).map(r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.ok().<Void>build());
    }

    @PutMapping("/insert/")
    Mono<Tva> insert(@RequestBody Tva tva) {
        return tvas.save(tva);
    }

    @PutMapping("/update/")
    Mono<Tva> update(@RequestBody Tva tva) {
        return tvas.findById(tva.getId()).map(foundItem -> {
            foundItem.setTaux(tva.getTaux());
            return foundItem;
        }).flatMap(tvas::save);
    }
}
