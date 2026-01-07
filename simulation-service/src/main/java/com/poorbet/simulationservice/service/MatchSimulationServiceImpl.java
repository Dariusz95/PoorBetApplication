package com.poorbet.simulationservice.service;

import com.poorbet.simulationservice.dto.LiveMatchEvent;
import com.poorbet.simulationservice.dto.TeamStatsDto;
import com.poorbet.simulationservice.model.MatchContext;
import com.poorbet.simulationservice.request.SimulationBatchRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
public class MatchSimulationServiceImpl implements MatchSimulationService {

    private final Random random = new Random();

    @Override
    public Flux<LiveMatchEvent> simulateMatchLive(MatchContext context) {

        log.info("🚀 === simulateMatchLive");

        int totalMinutes = 90;

        LiveMatchEvent initial = new LiveMatchEvent(
                context.matchId(),
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

    public LiveMatchEvent simulateMatchFinal(MatchContext context) {

        int totalMinutes = 90;

        LiveMatchEvent prev = new LiveMatchEvent(
                context.matchId(),
                0,
                0,
                0,
                false
        );

        for (int minute = 1; minute <= totalMinutes; minute++) {
            prev = simulateMinute(prev, minute, context);
        }

        return new LiveMatchEvent(
                prev.matchId(),
                totalMinutes,
                prev.homeGoals(),
                prev.awayGoals(),
                true
        );
    }

    @Override
    public List<LiveMatchEvent> simulateBatch(SimulationBatchRequest request) {
        return request.matches().parallelStream()
                .map(match -> {
                    UUID matchId = (match.matchId() != null) ? match.matchId() :  UUID.randomUUID();
                    MatchContext context = new MatchContext(matchId, match.home(), match.away());
                    return simulateMatchFinal(context);
                })
                .toList();
    }


    private LiveMatchEvent simulateMinute(
            LiveMatchEvent prev,
            int minute,
            MatchContext context
    ) {
        boolean homeHasBall = random.nextBoolean();

        boolean goal = random.nextDouble() < calculateGoalChance(context.home(), context.away(), homeHasBall);

        int homeGoals = prev.homeGoals();
        int awayGoals = prev.awayGoals();

        if (goal) {
            if (homeHasBall) {
                homeGoals++;
            } else {
                awayGoals++;
            }
        }

        return new LiveMatchEvent(prev.matchId(),
                minute,
                homeGoals,
                awayGoals,
                minute == 90);
    }

    private double calculateGoalChance(
            TeamStatsDto home,
            TeamStatsDto away,
            boolean homeHasBall
    ) {
        double attack = homeHasBall ? home.attackPower() : away.attackPower();
        double defence = homeHasBall ? away.defencePower() : home.defencePower();

        double strength = attack / (attack + defence);

        double base = 0.02;
        double advantage = homeHasBall ? 0.005 : 0.0;

        return base + strength * 0.02 + advantage;
    }
}
