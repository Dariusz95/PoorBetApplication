package com.poorbet.matchservice.match.stream.service;

import com.poorbet.matchservice.match.stream.client.TeamsClient;
import com.poorbet.matchservice.match.stream.dto.TeamStatsDto;
import com.poorbet.matchservice.match.stream.model.LiveMatchEvent;
import com.poorbet.matchservice.match.stream.model.Match;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchSimulationServiceImpl implements MatchSimulationService {

    private final Random random = new Random();
    private final TeamsClient teamsClient;

    @Override
    public Flux<LiveMatchEvent> simulateMatchLive(Match match) {
        log.info("🚀 === simulateMatchLive");
//        TeamStatsDto home = teamsClient.getTeamStats(match.getHomeTeamId());
//        TeamStatsDto away = teamsClient.getTeamStats(match.getAwayTeamId());

        TeamStatsDto home = new TeamStatsDto(match.getHomeTeamId(), 20, 30);
        TeamStatsDto away = new TeamStatsDto(match.getAwayTeamId(), 20, 30);

        int totalMinutes = 90;

        return Flux
                .interval(Duration.ofSeconds(1))
                .take(totalMinutes)
                .scan(
                        new LiveMatchEvent(match.getMatchId(),
                                match.getHomeTeamId(),
                                match.getAwayTeamId(),
                                0,
                                0,
                                0,
                                false),
                        (prev, tick) -> simulateMinute(prev, tick.intValue() + 1, home, away, match)
                );
    }

    private LiveMatchEvent simulateMinute(
            LiveMatchEvent prev,
            int minute,
            TeamStatsDto home,
            TeamStatsDto away,
            Match match
    ) {
        boolean homeHasBall = random.nextBoolean();

        boolean goal = random.nextDouble() < calculateGoalChance(home, away, homeHasBall);

        int homeGoals = prev.getHomeScore();
        int awayGoals = prev.getAwayScore();

        if (goal) {
            if (homeHasBall) {
                homeGoals++;
            } else {
                awayGoals++;
            }
        }

        return new LiveMatchEvent(match.getMatchId(),
                match.getHomeTeamId(),
                match.getAwayTeamId(),minute, homeGoals, awayGoals, minute == 90);
    }

    private double calculateGoalChance(TeamStatsDto home, TeamStatsDto away, boolean homeHasBall) {

        double attack = homeHasBall ? home.getAttack() : away.getAttack();
        double defence = homeHasBall ? away.getDefence() : home.getDefence();

        double ratio = attack / (attack + defence);

        return 0.005 + (ratio - 0.5) * 0.02;
    }
}