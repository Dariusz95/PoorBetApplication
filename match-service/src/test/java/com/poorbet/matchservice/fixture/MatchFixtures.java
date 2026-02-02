package com.poorbet.matchservice.fixture;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.poorbet.matchservice.match.match.domain.Match;
import com.poorbet.matchservice.match.match.domain.MatchStatus;
import com.poorbet.matchservice.match.match.domain.Odds;
import com.poorbet.matchservice.match.match.dto.TeamStatsDto;
import com.poorbet.matchservice.match.matchpool.domain.MatchPool;
import com.poorbet.matchservice.match.matchpool.domain.PoolStatus;

public class MatchFixtures {

    public static final UUID MATCH_ID_1 = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    public static final UUID MATCH_ID_2 = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");

    public static final UUID POOL_ID_1 = UUID.fromString("550e8400-e29b-41d4-a716-446655450001");

    public static final UUID HOME_TEAM_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655460001");
    public static final UUID AWAY_TEAM_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655460002");

    public static final BigDecimal HOME_WIN_ODDS = new BigDecimal("1.50");
    public static final BigDecimal DRAW_ODDS = new BigDecimal("3.25");
    public static final BigDecimal AWAY_WIN_ODDS = new BigDecimal("5.00");

    public static Match createMatch(UUID matchId, UUID homeTeamId, UUID awayTeamId, MatchStatus status) {
        return Match.builder()
                .id(matchId)
                .homeTeamId(homeTeamId)
                .awayTeamId(awayTeamId)
                .homeGoals(0)
                .awayGoals(0)
                .status(status)
                .build();
    }

    public static Match createMatch() {
        return createMatch(MATCH_ID_1, HOME_TEAM_ID, AWAY_TEAM_ID, MatchStatus.SCHEDULED);
    }

    public static List<Match> createMatches(int count) {
        List<Match> matches = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            matches.add(createMatch());
        }
        return matches;
    }

    public static Odds createOdds(BigDecimal homeWin, BigDecimal draw, BigDecimal awayWin) {
        return Odds.builder()
                .id(UUID.randomUUID())
                .homeWin(homeWin)
                .draw(draw)
                .awayWin(awayWin)
                .build();
    }

    public static Odds createOdds() {
        return createOdds(HOME_WIN_ODDS, DRAW_ODDS, AWAY_WIN_ODDS);
    }

    public static MatchPool createMatchPool(PoolStatus status) {
        return MatchPool.builder()
                .id(POOL_ID_1)
                .status(status)
                .scheduledStartTime(OffsetDateTime.now().plusHours(2))
                .matches(new ArrayList<>())
                .build();
    }

    public static MatchPool createMatchPool() {
        return createMatchPool(PoolStatus.BETTABLE);
    }

    public static TeamStatsDto createTeamStats(UUID teamId) {
        return TeamStatsDto.builder()
                .id(teamId)
                .attackPower(75)
                .defencePower(70)
                .build();
    }

    public static TeamStatsDto createTeamStats(UUID teamId, int attackPower, int defencePower) {
        return TeamStatsDto.builder()
                .id(teamId)
                .attackPower(attackPower)
                .defencePower(defencePower)
                .build();
    }

    public static List<TeamStatsDto> createTeamStatsList(int count) {
        List<TeamStatsDto> stats = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            stats.add(createTeamStats(UUID.randomUUID()));
        }
        return stats;
    }
}
