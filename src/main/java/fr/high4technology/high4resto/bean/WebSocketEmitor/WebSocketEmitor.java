package fr.high4technology.high4resto.bean.WebSocketEmitor;

import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

public class WebSocketEmitor {
    EmitterProcessor<String> emitterProcessor;

    public Flux<String> getPublisher() {
        emitterProcessor = EmitterProcessor.create();
        return emitterProcessor;
    }

    public void onNext(String nextString) {
        if (emitterProcessor != null)
            emitterProcessor.onNext(nextString);
    }

    public void complete() {
        emitterProcessor.onComplete();
    }
}
