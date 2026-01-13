package com.poorbet.matchservice.match.stream.service;


import com.poorbet.matchservice.match.stream.client.OddsClient;
import com.poorbet.matchservice.match.stream.client.TeamsClient;
import com.poorbet.matchservice.match.stream.config.MatchPoolProperties;
import com.poorbet.matchservice.match.stream.dto.WinProbabilityDto;
import com.poorbet.matchservice.match.stream.dto.request.PreMatchDto;
import com.poorbet.matchservice.match.stream.dto.request.PredictionBatchRequestDto;
import com.poorbet.matchservice.match.stream.dto.TeamStatsDto;
import com.poorbet.matchservice.match.stream.dto.response.BatchOddsResponse;
import com.poorbet.matchservice.match.stream.mapper.PredictionBatchMapper;
import com.poorbet.matchservice.match.stream.model.Match;
import com.poorbet.matchservice.match.stream.model.MatchPool;
import com.poorbet.matchservice.match.stream.model.Odds;
import com.poorbet.matchservice.match.stream.model.enums.MatchStatus;
import com.poorbet.matchservice.match.stream.model.enums.PoolStatus;
import com.poorbet.matchservice.match.stream.repository.MatchPoolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class MatchPoolSchedulingServiceImpl implements MatchPoolSchedulingService {

    private final MatchPoolRepository matchPoolRepository;
    private final MatchPoolProperties properties;
    private final TeamsClient teamsClient;
    private final MatchPoolService matchPoolService;
    private final TaskScheduler taskScheduler;
    private final OddsClient oddsClient;

    @Value("${INSTANCE_ID:local}")
    private String instanceId;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void scheduleMissingPools() {
        log.info("🔁 scheduleMissingPools()");

        int poolsInAdvance = properties.getPoolsInAdvance();
        int intervalMinutes = properties.getPoolIntervalMinutes();

        OffsetDateTime now = OffsetDateTime.now();

        List<MatchPool> futurePools =
                matchPoolRepository.findFuturePools(PoolStatus.BETTABLE, now);

        OffsetDateTime nextStartTime = now.plusMinutes(intervalMinutes);

        if (!futurePools.isEmpty()) {
            OffsetDateTime lastTime = futurePools.get(futurePools.size() - 1).getScheduledStartTime();
            if (lastTime.isAfter(nextStartTime)) {
                nextStartTime = lastTime.plusMinutes(intervalMinutes);
            }
        }

        log.info("📊 Pule w kolejce: {}/{}", futurePools.size(), poolsInAdvance);

        int missingPools = poolsInAdvance - futurePools.size();

        if (missingPools <= 0) return;

        for (int i = 0; i < missingPools; i++) {
            MatchPool pool = MatchPool.builder()
                    .status(PoolStatus.BETTABLE)
                    .scheduledStartTime(nextStartTime)
                    .build();

            List<TeamStatsDto> teams;
            try {
                teams = teamsClient.randomTeams(4);
            } catch (Exception ex) {
                log.error("Cannot fetch teams", ex);
                break;
            }

            addMatchesToPool(teams, pool);

            matchPoolRepository.save(pool);


            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            log.info("✅ Instance {}: created pool {} scheduled for {}", instanceId, pool.getId(), pool.getScheduledStartTime());
                            schedulePool(pool);
                        }
                    }
            );

            nextStartTime = nextStartTime.plusMinutes(intervalMinutes);
        }
    }

    private void schedulePool(MatchPool pool) {
        if (pool.getStatus() != PoolStatus.BETTABLE) {
            return;
        }

        taskScheduler.schedule(
                () -> matchPoolService.startPool(pool.getId()),
                pool.getScheduledStartTime().toInstant()
        );

        log.info("Scheduled pool {} at {}", pool.getId(), pool.getScheduledStartTime());
    }

    private List<Match> addMatchesToPool(List<TeamStatsDto> teams, MatchPool pool) {

        if (teams.size() % 2 != 0) {
            throw new IllegalArgumentException("Number of teams must be even");
        }

        Collections.shuffle(teams);


        List<PreMatchDto> preMatches = getPreMatches(teams);

        log.info("xxxxc preMatches -> {}", preMatches);

        PredictionBatchRequestDto batchRequest = PredictionBatchMapper.toPredictionBatchRequestDto(preMatches);

        Map<UUID, BatchOddsResponse> oddsMap = oddsClient.getBatchPrediction(batchRequest)
                .getMatches()
                .stream()
                .collect(Collectors.toMap(
                        BatchOddsResponse::matchId,
                        Function.identity()
                ));

        List<Match> matches = new ArrayList<>();

        log.info("xxxxc odds response -> {}", oddsMap);


        for (PreMatchDto preMatch : preMatches) {
            BatchOddsResponse oddsResponse = oddsMap.get(preMatch.getMatchId());

            if (oddsResponse == null) {
                log.warn("No odds returned for match {}", preMatch.getMatchId());
                continue;
            }

            Match match = Match.builder()
                    .pool(pool)
                    .homeTeamId(preMatch.getHomeTeamId())
                    .awayTeamId(preMatch.getAwayTeamId())
                    .homeGoals(0)
                    .awayGoals(0)
                    .status(MatchStatus.SCHEDULED)
                    .build();

            match.setOdds(calculateOdds(oddsResponse.oddsResponse()));
            pool.addMatch(match);
            matches.add(match);
        }

        return matches;
    }

    private List<PreMatchDto> getPreMatches(List<TeamStatsDto> teams) {
        List<PreMatchDto> preMatches = new ArrayList<>();

        for (int i = 0; i < teams.size(); i += 2) {
            TeamStatsDto home = teams.get(i);
            TeamStatsDto away = teams.get(i + 1);

            PreMatchDto preMatch = PreMatchDto.builder()
                    .matchId(UUID.randomUUID())
                    .homeTeamId(home.getId())
                    .awayTeamId(away.getId())
                    .homeAttack(home.getAttackPower())
                    .homeDefense(home.getDefencePower())
                    .awayAttack(away.getAttackPower())
                    .awayDefense(away.getDefencePower())
                    .build();

            preMatches.add(preMatch);
        }

        return preMatches;
    }

    private Odds calculateOdds(WinProbabilityDto dto) {
        double sum = dto.homeWinProbability() + dto.drawProbability() + dto.awayWinProbability();


        double homeOdd = truncateToTwoDecimalPlaces(sum / dto.homeWinProbability());
        double drawOdd = truncateToTwoDecimalPlaces(sum / dto.drawProbability());
        double awayOdd = truncateToTwoDecimalPlaces(sum / dto.awayWinProbability());

        return Odds.builder()
                .homeWin(homeOdd)
                .draw(drawOdd)
                .awayWin(awayOdd)
                .build();
    }

    private double truncateToTwoDecimalPlaces(double value){
        return Math.floor(value * 100) / 100;
    }
}



