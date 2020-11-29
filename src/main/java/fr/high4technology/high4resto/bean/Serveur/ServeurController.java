package fr.high4technology.high4resto.bean.Serveur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

import fr.high4technology.high4resto.bean.ItemCarte.ItemCarte;
import fr.high4technology.high4resto.bean.ItemCategorie.ItemCategorie;
import fr.high4technology.high4resto.bean.ItemCategorie.ItemCategorieRepository;
import fr.high4technology.high4resto.bean.Stock.Stock;
import fr.high4technology.high4resto.bean.Stock.StockRepository;
import fr.high4technology.high4resto.bean.Tracability.PreOrder.PreOrder;

@RestController
@RequestMapping("/api/serveur")
@RequiredArgsConstructor
public class ServeurController {
    @Autowired
    private StockRepository stocks;
    @Autowired
    private ItemCategorieRepository itemCategories;

    @GetMapping("/findCategory/")
    public Flux<ItemCategorie> getAll() {
        return itemCategories.findAll();
    }

    @GetMapping("/findStocks/{idCategorie}")
    public Flux<Stock> getGrouped(@PathVariable String idCategorie) {
        return stocks.findAll().filter(stock->stock.getItem().getCategorie().getId().equals(idCategorie)).sort((a, b) -> {
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

    /*

    @PutMapping("/moveToPreorder/")
    Mono<PreOrder> moveToPreorder(@RequestBody Stock stock)
    {

    }

    @PutMapping("/moveToPreorder/")
    Mono<Order> moveToOrder(@RequestBody PreOrder preOrder)
    {

    }


    */
}
