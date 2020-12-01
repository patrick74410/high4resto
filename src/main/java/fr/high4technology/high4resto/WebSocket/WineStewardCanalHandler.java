package fr.high4technology.high4resto.WebSocket;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import fr.high4technology.high4resto.bean.WebSocketEmitor.WebSocketEmitor;
import reactor.core.publisher.Mono;

@Component("WINESTEWARD-CANAL")
public class WineStewardCanalHandler implements WebSocketHandler {
    private WebSocketEmitor emitor = new WebSocketEmitor();

    public void sendMessage(String message) {
        this.emitor.onNext(message);
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        return webSocketSession.send(emitor.getPublisher().map(webSocketSession::textMessage))
                .and(webSocketSession.receive().map(WebSocketMessage::getPayloadAsText).log());
    }
}
