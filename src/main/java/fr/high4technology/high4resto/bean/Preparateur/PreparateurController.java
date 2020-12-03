package fr.high4technology.high4resto.bean.Preparateur;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.high4technology.high4resto.Util.Util;
import fr.high4technology.high4resto.WebSocket.ServerCanalHandler;
import fr.high4technology.high4resto.bean.ItemCategorie.ItemCategorieRepository;
import fr.high4technology.high4resto.bean.ItemPreparation.ItemPreparation;
import fr.high4technology.high4resto.bean.ItemPreparation.ItemPreparationRepository;
import fr.high4technology.high4resto.bean.Tracability.Order.Order;
import fr.high4technology.high4resto.bean.Tracability.Order.OrderRepository;
import fr.high4technology.high4resto.bean.Tracability.toPrepare.ToPrepare;
import fr.high4technology.high4resto.bean.Tracability.toPrepare.ToPrepareRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/preparateur")
@RequiredArgsConstructor
public class PreparateurController {
    @Autowired
    private ItemCategorieRepository itemCategories;
    @Autowired
    private OrderRepository orders;
    @Autowired
    private ServerCanalHandler serverCanal;
    @Autowired
    private ItemPreparationRepository preparations;
    @Autowired
    private ToPrepareRepository toPrepares;


    private void sendToServer(String message)
    {
        serverCanal.sendMessage(message);;
    }

    @PutMapping("/moveToPrepare/")
    Mono<ToPrepare> moveToTake(@RequestBody ToPrepare toPrepare)
    {
        toPrepare.setInside(Util.getTimeNow());
        return this.orders.deleteById(toPrepare.getOrder().getId()).then(toPrepares.save(toPrepare));
    }

    @GetMapping("/findToPrepare/{role}")
    public Flux<ToPrepare> getToPrepare(@PathVariable String role){
        Queue<ItemPreparation> preparations = new ConcurrentLinkedQueue<ItemPreparation>();
        return this.preparations.findAll().flatMap(preparationss->
        {
            preparations.add(preparationss);
            return Mono.empty();

        }).thenMany(this.toPrepares.findAll())
        .filter(toPrepare->{
            List<String> roles=new ArrayList<String>();
            for(ItemPreparation preparation:preparations)
            {
                if(preparation.getId().equals(toPrepare.getOrder().getPreOrder().getStock().getItem().getId()))
                {
                    roles.addAll(preparation.getRoleName());
                    break;
                }
            }
            for(String rrole:roles)
            {
                if(rrole.equals(role))
                return true;
            }
            return false;
        }).sort((a,b)->{
            return a.getOrder().getPreOrder().getDestination().compareTo(b.getOrder().getPreOrder().getDestination());
        });
    }

    @GetMapping("/findSignalOrder/{role}")
    public Flux<Order> getSignalOrder(@PathVariable String role){
        Queue<ItemPreparation> preparations = new ConcurrentLinkedQueue<ItemPreparation>();
        return this.preparations.findAll().flatMap(preparationss->
        {
            preparations.add(preparationss);
            return Mono.empty();

        }).thenMany(this.orders.findAll().filter(order->!order.isToTake()))
        .filter(order->{
            List<String> roles=new ArrayList<String>();
            for(ItemPreparation preparation:preparations)
            {
                if(preparation.getId().equals(order.getPreOrder().getStock().getItem().getId()))
                {
                    roles.addAll(preparation.getRoleName());
                    break;
                }
            }
            for(String rrole:roles)
            {
                if(rrole.equals(role))
                return true;
            }
            return false;
        }).sort((a,b)->{
            return a.getPreOrder().getDestination().compareTo(b.getPreOrder().getDestination());
        });
    }

    @GetMapping("/findToTakeOrder/{role}")
    public Flux<Order> getToTake(@PathVariable String role){
        Queue<ItemPreparation> preparations = new ConcurrentLinkedQueue<ItemPreparation>();
        return this.preparations.findAll().flatMap(preparationss->
        {
            preparations.add(preparationss);
            return Mono.empty();

        }).thenMany(this.orders.findAll().filter(order->order.isToTake()))
        .filter(order->{
            List<String> roles=new ArrayList<String>();
            for(ItemPreparation preparation:preparations)
            {
                if(preparation.getId().equals(order.getPreOrder().getStock().getItem().getId()))
                {
                    roles.addAll(preparation.getRoleName());
                    break;
                }
            }
            for(String rrole:roles)
            {
                if(rrole.equals(role))
                return true;
            }
            return false;
        }).sort((a,b)->{
            return a.getPreOrder().getDestination().compareTo(b.getPreOrder().getDestination());
        });
    }
}
