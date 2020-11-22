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
import java.util.ArrayList;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {
    @Autowired
    private StockRepository stocks;

    @GetMapping("/find/")
    public Flux<Stock> getAll()
    {
        return stocks.findAll();
    } 

    @PutMapping("/insert/")
    Mono<Stock> insert(@RequestBody Stock stock)
    {
        return stocks.save(Stock.builder().item(stock.getItem()).disponibility(stock.getDisponibility()).build());
    }   

    @PutMapping("/insert/{qty}")
    Flux<Stock> insertMany(@RequestBody Stock stock,@PathVariable int qty)
    {
        List<Stock> manyStock=new ArrayList<Stock>();
        for(int i=0;i!=qty;i++)
        {
            manyStock.add(Stock.builder().item(stock.getItem()).disponibility(stock.getDisponibility()).build());
        }
        return stocks.saveAll(manyStock);
    }

    @DeleteMapping("/move/{idStock}/{destination}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String idStock,@PathVariable String destination)
    {
 /*!!!!!!! Reste a ajouter le transfer de l'item)*/ 
            return        
            stocks.deleteById(idStock).map(r -> ResponseEntity.ok().<Void>build())
            .defaultIfEmpty(ResponseEntity.ok().<Void>build());   
    }


}
