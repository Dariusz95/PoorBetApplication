package com.poorbet.matchservice.match.stream.service;

import com.poorbet.matchservice.match.stream.model.Match;
import com.poorbet.matchservice.match.stream.model.MatchPool;
import com.poorbet.matchservice.match.stream.model.MatchStatus;
import com.poorbet.matchservice.match.stream.repository.MatchRepository;
import com.poorbet.matchservice.match.stream.simulation.LiveMatchSimulation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MatchPoolSimulationService {

    private final MatchSimulationService matchSimulationService;
    private final LiveMatchSimulationManager liveManager;
    private final MatchRepository matchRepository;

    public void startPoolSimulation(MatchPool pool) {
        for (Match match : pool.getMatches()) {

            LiveMatchSimulation liveSimulation = liveManager.startIfNotRunning(match);

            matchSimulationService.simulateMatchLive(match)
                    .doOnNext(event -> {
                        liveSimulation.publish(event);

                        if(event.isFinished()){
                            this.onMatchFinished(match);
                        }
                    })
                    .subscribe();
        }
    }

    public void onMatchFinished(Match match) {
        match.setStatus(MatchStatus.FINISHED);
        matchRepository.save(match);
    }
}

