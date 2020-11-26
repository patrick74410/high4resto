package fr.high4technology.high4resto.bean.Stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.ArrayList;

import fr.high4technology.high4resto.Util.Util;
import fr.high4technology.high4resto.bean.ItemCarte.ItemCarte;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    @Autowired
    private StockRepository stocks;

    @GetMapping("/find/")
    public Flux<Stock> getAll() {
        return stocks.findAll();
    }

    @GetMapping("/grouped/find/")
    public Flux<Stock> getGrouped() {
        return stocks.findAll().sort((a, b) -> {
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
                });
    }

    @PutMapping("/insert/{userName}")
    Mono<Stock> insert(@RequestBody Stock stock, @PathVariable String userName) {
        String inside = Util.getTimeNow();

        return stocks.save(Stock.builder().username(userName).inside(inside).item(stock.getItem()).build());
    }

    @PutMapping("/insert/{qty}/{userName}")
    Flux<Stock> insertMany(@RequestBody Stock stock, @PathVariable int qty, @PathVariable String userName) {
        String inside = Util.getTimeNow();
        List<Stock> manyStock = new ArrayList<Stock>();
        for (int i = 0; i != qty; i++) {
            manyStock.add(Stock.builder().inside(inside).username(userName).item(stock.getItem()).build());
        }
        return stocks.saveAll(manyStock);
    }

    @DeleteMapping("/delete/{idStock}/")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String idStock) {

        return stocks.deleteById(idStock).map(r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.ok().<Void>build());
    }

}
