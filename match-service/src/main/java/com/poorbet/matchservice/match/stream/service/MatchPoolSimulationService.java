package com.poorbet.matchservice.match.stream.service;

import com.poorbet.matchservice.match.stream.dto.TeamStatsDto;
import com.poorbet.matchservice.match.stream.model.LiveMatchEvent;
import com.poorbet.matchservice.match.stream.model.Match;
import com.poorbet.matchservice.match.stream.model.MatchPool;
import com.poorbet.matchservice.match.stream.model.MatchStatus;
import com.poorbet.matchservice.match.stream.repository.MatchRepository;
import com.poorbet.matchservice.match.stream.service.helper.MatchContext;
import com.poorbet.matchservice.match.stream.simulation.LiveMatchSimulation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;


@Service
@RequiredArgsConstructor
public class MatchPoolSimulationService {

    private final MatchSimulationService matchSimulationService;
    private final LiveMatchSimulationManager liveManager;
    private final MatchRepository matchRepository;
    private final MatchFinishService matchFinishService;

    public void startPoolSimulation(UUID poolId, List<TeamStatsDto> teamStats) {
        List<Match> matches = matchRepository.findByPoolId(poolId);

        Map<UUID, TeamStatsDto> teamsById = teamStats.stream()
                .collect(Collectors.toMap(
                        TeamStatsDto::getId,
                        Function.identity()
                ));

        Flux.fromIterable(matches)
                .flatMap(match -> {
                    TeamStatsDto home = teamsById.get(match.getHomeTeamId());
                    TeamStatsDto away = teamsById.get(match.getAwayTeamId());

                    return startMatchSimulation(match.getMatchId(), home, away);
                })
                .subscribe();

    }

    private Mono<Void> startMatchSimulation(UUID matchId,
                                            TeamStatsDto home,
                                            TeamStatsDto away) {
        LiveMatchSimulation liveSimulation =
                liveManager.startIfNotRunning(matchId);

        return matchSimulationService.simulateMatchLive(new MatchContext(matchId, home, away))
                .doOnNext(event -> handleEvent(event, liveSimulation))
                .then();
    }

    private void handleEvent(
            LiveMatchEvent event,
            LiveMatchSimulation liveSimulation
    ) {
        liveSimulation.publish(event);

        if (event.isFinished()) {
            matchFinishService.finishMatch(event);
        }
    }
}

