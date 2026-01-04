package com.poorbet.matchservice.match.stream.service;

import com.poorbet.matchservice.match.stream.dto.TeamStatsDto;
import com.poorbet.matchservice.match.stream.model.LiveMatchEvent;
import com.poorbet.matchservice.match.stream.model.Match;
import com.poorbet.matchservice.match.stream.service.helper.MatchContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchSimulationServiceImpl implements MatchSimulationService {

    private final Random random = new Random();

    @Override
    public Flux<LiveMatchEvent> simulateMatchLive(MatchContext context) {
        log.info("🚀 === simulateMatchLive");

        int totalMinutes = 90;

        LiveMatchEvent initial = new LiveMatchEvent(
                context.matchId(),
                context.home().getId(),
                context.away().getId(),
                0,
                0,
                0,
                false
        );

        return Flux
                .interval(Duration.ofSeconds(1))
                .take(totalMinutes)
                .scan(initial, (prev, tick) -> simulateMinute(prev, tick.intValue() + 1, context));
    }

    private LiveMatchEvent simulateMinute(
            LiveMatchEvent prev,
            int minute,
            MatchContext context
    ) {
        log.debug("⏱ minute={} home={} away={}", minute,
                prev.getHomeScore(),
                prev.getAwayScore());

        boolean homeHasBall = random.nextBoolean();

        boolean goal = random.nextDouble() < calculateGoalChancee(context.home(), context.away(), homeHasBall);
//        boolean goal = random.nextDouble() < calculateGoalChance(context.home(), context.away(), homeHasBall);

        int homeGoals = prev.getHomeScore();
        int awayGoals = prev.getAwayScore();

        if (goal) {
            if (homeHasBall) {
                homeGoals++;
            } else {
                awayGoals++;
            }
        }

        return new LiveMatchEvent(prev.getId(),
                context.home().getId(),
                context.away().getId(),
                homeGoals,
                awayGoals,
                minute,
                minute == 90);
    }

    private double calculateGoalChance(TeamStatsDto home, TeamStatsDto away, boolean homeHasBall) {

        double attack = homeHasBall ? home.getAttackPower() : away.getAttackPower();
        double defence = homeHasBall ? away.getDefencePower() : home.getDefencePower();

        double ratio = attack / (attack + defence);

        return 0.005 + (ratio - 0.5) * 0.02;
    }

    private double calculateGoalChancee(
            TeamStatsDto home,
            TeamStatsDto away,
            boolean homeHasBall
    ) {
        double attack = homeHasBall ? home.getAttackPower() : away.getAttackPower();
        double defence = homeHasBall ? away.getDefencePower() : home.getDefencePower();

        double strength = attack / (attack + defence);

        double base = 0.02;
        double advantage = homeHasBall ? 0.005 : 0.0;

        return base + strength * 0.02 + advantage;
    }
}