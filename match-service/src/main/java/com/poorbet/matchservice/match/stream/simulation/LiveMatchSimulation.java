package com.poorbet.matchservice.match.stream.simulation;

import com.poorbet.matchservice.match.stream.model.LiveMatchEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.UUID;

public class LiveMatchSimulation {
    private final UUID matchId;
    private final Sinks.Many<LiveMatchEvent> sink;


//    public LiveMatchSimulation(UUID matchId){
//        this.matchId = matchId;
//    }

    public LiveMatchSimulation(UUID matchId, Sinks.Many<LiveMatchEvent> sink){
        this.matchId = matchId;
        this.sink = sink;
    }

    public void publish(LiveMatchEvent event) {
        sink.tryEmitNext(event);
    }

    public Flux<LiveMatchEvent> stream() {
        return sink.asFlux();
    }

}
