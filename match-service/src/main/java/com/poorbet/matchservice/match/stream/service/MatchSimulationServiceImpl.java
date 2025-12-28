package com.poorbet.matchservice.match.stream.service;

import com.poorbet.matchservice.match.stream.client.TeamsClient;
import com.poorbet.matchservice.match.stream.dto.LiveMatchEvent;
import com.poorbet.matchservice.match.stream.dto.SimulateMatchRequest;
import com.poorbet.matchservice.match.stream.dto.TeamStatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MatchSimulationServiceImpl implements MatchSimulationService {

    private final Random random = new Random();
    private final TeamsClient teamsClient;

    @Override
    public Flux<LiveMatchEvent> simulateMatchLive(SimulateMatchRequest request) {

        TeamStatsDto home = teamsClient.getTeamStats(request.getHomeTeamId());
        TeamStatsDto away = teamsClient.getTeamStats(request.getAwayTeamId());

        int totalMinutes = 90;

        return Flux
                .interval(Duration.ofSeconds(1))
                .take(totalMinutes)
                .scan(
                        new LiveMatchEvent(0, 0, 0),
                        (prev, tick) -> simulateMinute(prev, tick.intValue() + 1, home, away)
                );
    }

    private LiveMatchEvent simulateMinute(
            LiveMatchEvent prev,
            int minute,
            TeamStatsDto home,
            TeamStatsDto away
    ) {
        boolean homeHasBall = random.nextBoolean();

        boolean goal = random.nextDouble() < calculateGoalChance(home, away, homeHasBall);

        int homeGoals = prev.getHomeGoals();
        int awayGoals = prev.getAwayGoals();

        if (goal) {
            if (homeHasBall) {
                homeGoals++;
            } else {
                awayGoals++;
            }
        }

        return new LiveMatchEvent(minute, homeGoals, awayGoals);
    }

    private double calculateGoalChance(TeamStatsDto home, TeamStatsDto away, boolean homeHasBall) {

        double attack = homeHasBall ? home.getAttack() : away.getAttack();
        double defence = homeHasBall ? away.getDefence() : home.getDefence();

        double ratio = attack / (attack + defence);

        return 0.005 + (ratio - 0.5) * 0.02;
    }
}