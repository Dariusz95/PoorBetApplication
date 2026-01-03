package com.poorbet.matchservice.match.stream.service;

import com.poorbet.matchservice.match.stream.model.LiveMatchEvent;
import com.poorbet.matchservice.match.stream.model.Match;
import com.poorbet.matchservice.match.stream.model.MatchPool;
import com.poorbet.matchservice.match.stream.model.MatchStatus;
import com.poorbet.matchservice.match.stream.repository.MatchRepository;
import com.poorbet.matchservice.match.stream.simulation.LiveMatchSimulation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
public class MatchPoolSimulationService {

    private final MatchSimulationService matchSimulationService;
    private final LiveMatchSimulationManager liveManager;
    private final MatchRepository matchRepository;

    public void startPoolSimulation(MatchPool pool) {
        Flux.fromIterable(pool.getMatches())
                .flatMap(this::startMatchSimulation)
                .subscribe();
    }

    private Mono<Void> startMatchSimulation(Match match) {
        LiveMatchSimulation liveSimulation = liveManager.startIfNotRunning(match);

        return matchSimulationService.simulateMatchLive(match)
                .doOnNext(event -> handleEvent(event, liveSimulation, match))
                .then();
    }

    private void handleEvent(
            LiveMatchEvent event,
            LiveMatchSimulation liveSimulation,
            Match match
    ) {
        liveSimulation.publish(event);

        if (event.isFinished()) {
            onMatchFinished(match);
        }
    }

//    public void startPoolSimulation(MatchPool pool) {
//        for (Match match : pool.getMatches()) {
//
//            LiveMatchSimulation liveSimulation = liveManager.startIfNotRunning(match);
//
//            matchSimulationService.simulateMatchLive(match)
//                    .doOnNext(event -> {
//                        liveSimulation.publish(event);
//
//                        if(event.isFinished()){
//                            this.onMatchFinished(match);
//                        }
//                    })
//                    .subscribe();
//        }
//    }

    public void onMatchFinished(Match match) {
        match.setStatus(MatchStatus.FINISHED);
        matchRepository.save(match);
    }
}

