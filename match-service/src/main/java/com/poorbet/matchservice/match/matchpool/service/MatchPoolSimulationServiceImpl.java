package com.poorbet.matchservice.match.matchpool.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.poorbet.matchservice.team.dto.TeamStatsDto;
import org.springframework.stereotype.Service;

import com.poorbet.matchservice.match.client.OddsEngineClient;
import com.poorbet.matchservice.match.match.domain.Match;
import com.poorbet.matchservice.match.match.dto.request.SimulationRequest;
import com.poorbet.matchservice.match.match.dto.request.SimulationTeamStats;
import com.poorbet.matchservice.match.match.repository.MatchRepository;
import com.poorbet.matchservice.match.matchpool.dto.LiveMatchEventDto;
import com.poorbet.matchservice.match.matchpool.simulation.LiveMatchSimulation;
import com.poorbet.matchservice.team.service.TeamService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Slf4j
@Service
@RequiredArgsConstructor
public class MatchPoolSimulationServiceImpl implements MatchPoolSimulationService {

    private final LiveMatchSimulationManager liveManager;
    private final MatchRepository matchRepository;
    private final MatchFinishService matchFinishService;
    private final OddsEngineClient oddsEngineClient;
    private final TeamService teamService;

    public void startPoolSimulation(UUID poolId) {
        List<Match> matches = matchRepository.findByPoolId(poolId);

        List<UUID> teamIds = matches.stream()
                .flatMap(m -> Stream.of(m.getHomeTeamId(), m.getAwayTeamId()))
                .distinct()
                .toList();

        List<TeamStatsDto> teamStats = teamService.getStatsByIds(teamIds);

        Map<UUID, TeamStatsDto> teamsById = teamStats.stream()
                .collect(Collectors.toMap(
                        TeamStatsDto::getId,
                        Function.identity()
                ));

        Flux.fromIterable(matches)
                .flatMap(match -> {
                    TeamStatsDto home = teamsById.get(match.getHomeTeamId());
                    TeamStatsDto away = teamsById.get(match.getAwayTeamId());

                    return startMatchSimulation(match.getId(), home, away);
                })
                .subscribe(
                        null,
                        error -> log.error("Pool simulation failed for poolId={}: {}", poolId, error.getMessage(), error)
                );

    }

    private Mono<Void> startMatchSimulation(UUID matchId,
                                            TeamStatsDto home,
                                            TeamStatsDto away) {
        LiveMatchSimulation liveSimulation =
                liveManager.startIfNotRunning(matchId);

        SimulationRequest request = buildSimulationRequest(matchId, home, away);

        return oddsEngineClient.simulateMatch(request)
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

