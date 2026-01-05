package com.poorbet.matchservice.match.stream.service;

import com.poorbet.matchservice.match.stream.client.SimulationClient;
import com.poorbet.matchservice.match.stream.dto.TeamStatsDto;
import com.poorbet.matchservice.match.stream.dto.LiveMatchEventDto;
import com.poorbet.matchservice.match.stream.model.Match;
import com.poorbet.matchservice.match.stream.repository.MatchRepository;
import com.poorbet.matchservice.match.stream.request.SimulationRequest;
import com.poorbet.matchservice.match.stream.request.SimulationTeamStats;
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


@Service
@RequiredArgsConstructor
public class MatchPoolSimulationServiceImpl implements MatchPoolSimulationService {

    private final LiveMatchSimulationManager liveManager;
    private final MatchRepository matchRepository;
    private final MatchFinishService matchFinishService;
    private final SimulationClient simulationClient;

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

        SimulationRequest request = buildSimulationRequest(matchId, home, away);

        return simulationClient.simulateMatch(request)
                .map(event -> LiveMatchEventDto.fromEvent(event, home, away))
                .doOnNext(event -> handleEvent(event, liveSimulation))
                .then();
    }

    private void handleEvent(
            LiveMatchEventDto event,
            LiveMatchSimulation liveSimulation
    ) {
        liveSimulation.publish(event);

        if (event.isFinished()) {
            matchFinishService.finishMatch(event);
        }
    }

    private SimulationRequest buildSimulationRequest(UUID matchId, TeamStatsDto home, TeamStatsDto away) {
        SimulationTeamStats homeStats = new SimulationTeamStats(home.getAttackPower(), home.getDefencePower());
        SimulationTeamStats awayStats = new SimulationTeamStats(away.getAttackPower(), away.getDefencePower());

        return new SimulationRequest(matchId, homeStats, awayStats);
    }
}

