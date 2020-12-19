package fr.high4technology.high4resto.bean.Job;

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import fr.high4technology.high4resto.Util.Util;
import fr.high4technology.high4resto.bean.Client.ClientRepository;
import fr.high4technology.high4resto.bean.Stock.StockRepository;
import fr.high4technology.high4resto.bean.Tracability.PreOrder.PreOrder;
import fr.high4technology.high4resto.bean.Tracability.PreOrder.PreOrderRepository;
import reactor.core.publisher.Flux;

@Component
public class ScheduledTasks {
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {

        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

    @Autowired
    private ClientRepository clients;
    @Autowired
    private PreOrderRepository preorders;
    @Autowired
    private StockRepository stocks;


    @Scheduled(fixedRate = 1000 )
    public void reportCurrentTime() {
        Queue<PreOrder> globalQueue = new ConcurrentLinkedQueue<PreOrder>();
        var flux = clients.findAll().map(client->{
            int index=0;
            for(PreOrder item:client.getCommande().getItems())
            {
                try
                {
                    Date dateNow=Util.getDateNow();
                    Date dateItem=Util.parseDate(item.getInside());
                    log.warn(Long.toString(getDateDiff(dateItem,dateNow,TimeUnit.MINUTES)));
                    if(getDateDiff(dateItem,dateNow,TimeUnit.MINUTES)>20)
                    {
                        // remove from preorders lists
                        client.getCommande().getItems().remove(index);
                        // add to basket
                        client.getCurrentPanier().add(item.getStock().getItem());
                        // add to preOrdersRemove
                        globalQueue.add(item);
                        log.info("J'enlève la date");
                    }
                }
                catch(Exception e)
                {
                    log.info(e.getMessage());
                }
                index+=1;
            }
            return client;
        }).flatMap(clients::save)
        .thenMany(Flux.fromIterable(globalQueue)
        .flatMap(preOrder->{
            return stocks.save(preOrder.getStock());
        })
        .flatMap(stock->{
            return preorders.deleteById(stock.getId());
        }));

        flux.doOnSubscribe(data -> log.info("data:" + data)).thenMany(flux).subscribe(
                                data -> log.info("data:" + data), err -> log.error("error:" + err),
                                () -> log.info("done initialization..."));
	}
}
