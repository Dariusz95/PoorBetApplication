package com.poorbet.matchservice.match.stream.service;

import com.poorbet.matchservice.match.stream.dto.LiveMatchEventDto;
import com.poorbet.matchservice.match.stream.simulation.LiveMatchSimulation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class LiveMatchSimulationManager {

    private final Map<UUID, LiveMatchSimulation> simulations = new ConcurrentHashMap<>();
    private final Sinks.Many<LiveMatchEventDto> sink = Sinks.many().replay().limit(10);

    public LiveMatchSimulation startIfNotRunning(UUID matchId) {
        return simulations.computeIfAbsent(
                matchId,
                id -> new LiveMatchSimulation(id, sink)
        );
    }

    public Flux<LiveMatchEventDto> streamAll() {
        return sink.asFlux();
    }
}
