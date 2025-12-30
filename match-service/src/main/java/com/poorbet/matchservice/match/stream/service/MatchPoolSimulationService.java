package com.poorbet.matchservice.match.stream.service;

import com.poorbet.matchservice.match.stream.model.Match;
import com.poorbet.matchservice.match.stream.model.MatchPool;
import com.poorbet.matchservice.match.stream.model.MatchStatus;
import com.poorbet.matchservice.match.stream.simulation.LiveMatchSimulation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchPoolSimulationService {

    private final MatchSimulationService matchSimulationService;
    private final LiveMatchSimulationManager liveManager;

    public void startPoolSimulation(MatchPool pool) {
        for (Match match : pool.getMatches()) {

            LiveMatchSimulation liveSimulation = liveManager.startIfNotRunning(match);

            matchSimulationService.simulateMatchLive(match)
                    .doOnNext(liveSimulation::publish)
                    .doOnComplete(() -> {
                        match.setStatus(MatchStatus.FINISHED);
//                        liveManager.remove(match);
                    })
                    .subscribe();
        }
    }
}

