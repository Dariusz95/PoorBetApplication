package com.poorbet.matchservice.match.match.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poorbet.matchservice.match.match.domain.OddsType;
import com.poorbet.matchservice.match.match.dto.MatchBetSnapshotDto;
import com.poorbet.matchservice.match.match.repository.MatchRepository;
import com.poorbet.matchservice.team.dto.TeamStatsDto;
import com.poorbet.matchservice.team.service.TeamService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BetSnapshotService {

    private final MatchRepository matchRepository;
    private final TeamService teamService;
    private final OddsService oddsService;

    @Transactional(readOnly = true)
    public MatchBetSnapshotDto getBetSnapshot(UUID matchId, OddsType type) {
        var match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found: " + matchId));

        List<TeamStatsDto> teams = teamService.getStatsByIds(
                List.of(match.getHomeTeamId(), match.getAwayTeamId())
        );

        String homeTeamName = teams.stream()
                .filter(t -> t.getId().equals(match.getHomeTeamId()))
                .map(TeamStatsDto::getName)
                .findFirst()
                .orElse("Unknown");

        String awayTeamName = teams.stream()
                .filter(t -> t.getId().equals(match.getAwayTeamId()))
                .map(TeamStatsDto::getName)
                .findFirst()
                .orElse("Unknown");

        BigDecimal odd = oddsService.getOdds(matchId, type)
                .orElse(BigDecimal.ONE);

        OffsetDateTime matchStartTime = match.getPool().getScheduledStartTime();

        return new MatchBetSnapshotDto(
                matchId,
                homeTeamName,
                awayTeamName,
                matchStartTime,
                odd
        );
    }
}
