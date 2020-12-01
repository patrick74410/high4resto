package fr.high4technology.high4resto.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

@Configuration
public class ReactiveWebSocketConfiguration {

    @Autowired
    @Qualifier("WINESTEWARD-CANAL")
    private WebSocketHandler wineStewardCanal;
    @Autowired
    @Qualifier("BARWAITER-CANAL")
    private WebSocketHandler barWaiterCanal;
    @Autowired
    @Qualifier("SERVER-CANAL")
    private WebSocketHandler serverCanal;
    @Autowired
    @Qualifier("HOTCOOK-CANAL")
    private WebSocketHandler hotCookCanal;
    @Autowired
    @Qualifier("COLDCOOK-CANAL")
    private WebSocketHandler coldCookCanal;
    @Autowired
    @Qualifier("COOK-CANAL")
    private WebSocketHandler cookCanal;

    @Bean
    public HandlerMapping webSocketHandlerMapping() {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/api/canal/wineSteward", wineStewardCanal);
        map.put("/api/canal/barWaiter", barWaiterCanal);
        map.put("/api/canal/server", serverCanal);
        map.put("/api/canal/hotCook", hotCookCanal);
        map.put("/api/canal/coldCook", coldCookCanal);
        map.put("/api/canal/cook", cookCanal);

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(1);
        handlerMapping.setUrlMap(map);
        return handlerMapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}