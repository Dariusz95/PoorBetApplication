package com.poorbet.matchservice.match.stream.simulation;

import com.poorbet.matchservice.match.stream.model.LiveMatchEventDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.UUID;

public class LiveMatchSimulation {
    private final UUID matchId;
    private final Sinks.Many<LiveMatchEventDto> sink;

    public LiveMatchSimulation(UUID matchId, Sinks.Many<LiveMatchEventDto> sink){
        this.matchId = matchId;
        this.sink = sink;
    }

    public void publish(LiveMatchEventDto event) {
        sink.tryEmitNext(event);
    }

    public Flux<LiveMatchEventDto> stream() {
        return sink.asFlux();
    }

}
