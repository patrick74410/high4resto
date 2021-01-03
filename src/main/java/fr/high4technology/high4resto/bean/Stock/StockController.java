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
import fr.high4technology.high4resto.bean.ItemCarte.ItemCarteRepository;
import fr.high4technology.high4resto.bean.Tracability.Delevery.Delevery;
import fr.high4technology.high4resto.bean.Tracability.Order.Order;
import fr.high4technology.high4resto.bean.Tracability.PreOrder.PreOrder;
import fr.high4technology.high4resto.bean.Tracability.Prepare.Prepare;
import fr.high4technology.high4resto.bean.Tracability.ToDelivery.ToDelivery;
import fr.high4technology.high4resto.bean.Tracability.Trash.Trash;
import fr.high4technology.high4resto.bean.Tracability.Trash.TrashRepository;
import fr.high4technology.high4resto.bean.Tracability.toPrepare.ToPrepare;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    @Autowired
    private StockRepository stocks;

    @Autowired
    private TrashRepository trashs;

    @Autowired
    private ItemCarteRepository items;

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

    @GetMapping("/updateQty/{userName}/{itemId}/{qty}")
    public Mono<ResponseEntity<Void>> updateQty(@PathVariable String userName,@PathVariable String itemId,@PathVariable int qty)
    {
        return stocks.findAll().filter(stock -> stock.getItem().getId().equals(itemId))
        .collectList().flatMapMany(listToDelete->{
            ArrayList<Trash> insert = new ArrayList<Trash>();
            for(Stock stock:listToDelete)
            {
                stock.getItem().setStock(1);
                PreOrder tpPreorder=PreOrder.builder().id(stock.getId()).stock(stock).build();
                Order tpOrder=Order.builder().id(stock.getId()).preOrder(tpPreorder).build();
                ToPrepare tpToPrepare=ToPrepare.builder().order(tpOrder).id(stock.getId()).build();
                Prepare tpPrepare=Prepare.builder().id(stock.getId()).toPrepare(tpToPrepare).build();
                ToDelivery tpToDelivery=ToDelivery.builder().id(stock.getId()).prepare(tpPrepare).build();
                Delevery tpDelevery=Delevery.builder().toDelivery(tpToDelivery).id(stock.getId()).build();
                Trash tpTrash=Trash.builder().delevery(tpDelevery).causeMessage("Stock reset qty").inside(Util.getTimeNow()).id(stock.getId()).build();
                insert.add(tpTrash);
            }
            return trashs.saveAll(insert);
        }).flatMap(trash->{
            return Mono.just(trash.getDelevery().getToDelivery().getPrepare().getToPrepare().getOrder().getPreOrder().getStock());
        }).collectList()
        .flatMapMany(stocks::deleteAll)
        .then(items.findById(itemId))
        .flatMapMany(item->{
            ArrayList<Stock> stockList= new ArrayList<Stock>();
            Stock tpStock=Stock.builder().inside(Util.getTimeNow()).item(item).username(userName).build();
            for(int i=0;i!=qty;i+=1)
            {
                stockList.add(tpStock);
            }
            return stocks.saveAll(stockList);
        })
        .then(Mono.just(ResponseEntity.ok().<Void>build()));
    }

}
