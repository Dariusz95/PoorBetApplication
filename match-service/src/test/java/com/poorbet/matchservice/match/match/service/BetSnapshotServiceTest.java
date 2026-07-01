package com.poorbet.matchservice.match.match.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.poorbet.matchservice.match.match.domain.Match;
import com.poorbet.matchservice.match.match.domain.MatchStatus;
import com.poorbet.matchservice.match.match.domain.OddsType;
import com.poorbet.matchservice.match.match.dto.BetSnapshotRequest;
import com.poorbet.matchservice.match.match.dto.MatchBetSnapshotDto;
import com.poorbet.matchservice.match.match.repository.MatchRepository;
import com.poorbet.matchservice.match.matchpool.domain.MatchPool;
import com.poorbet.matchservice.match.matchpool.domain.PoolStatus;
import com.poorbet.matchservice.team.dto.TeamStatsDto;
import com.poorbet.matchservice.team.service.TeamService;

@ExtendWith(MockitoExtension.class)
@DisplayName("BetSnapshotService Unit Tests")
class BetSnapshotServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private TeamService teamService;

    @Mock
    private OddsService oddsService;

    @InjectMocks
    private BetSnapshotService betSnapshotService;

    private UUID matchId;
    private UUID homeTeamId;
    private UUID awayTeamId;
    private MatchPool pool;

    @BeforeEach
    void setUp() {
        matchId = UUID.randomUUID();
        homeTeamId = UUID.randomUUID();
        awayTeamId = UUID.randomUUID();

        pool = MatchPool.builder()
                .id(UUID.randomUUID())
                .status(PoolStatus.BETTABLE)
                .scheduledStartTime(OffsetDateTime.now().plusHours(2))
                .build();
    }

    private Match buildMatch(MatchStatus status) {
        Match match = Match.builder()
                .id(matchId)
                .homeTeamId(homeTeamId)
                .awayTeamId(awayTeamId)
                .status(status)
                .build();
        match.setPool(pool); // using setter since pool is a ManyToOne relation
        return match;
    }

    private TeamStatsDto teamStats(UUID teamId, String name) {
        return TeamStatsDto.builder()
                .id(teamId)
                .name(name)
                .attackPower(70)
                .defencePower(65)
                .build();
    }

    @Nested
    @DisplayName("getBetSnapshot — single snapshot")
    class GetBetSnapshot {

        @Test
        @DisplayName("Should return snapshot when match is SCHEDULED")
        void should_returnSnapshot_when_matchIsScheduled() {
            Match match = buildMatch(MatchStatus.SCHEDULED);
            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
            when(teamService.getStatsByIds(anyList())).thenReturn(List.of(
                    teamStats(homeTeamId, "Home FC"),
                    teamStats(awayTeamId, "Away FC")
            ));
            when(oddsService.getOdds(matchId, OddsType.HOME_WIN)).thenReturn(Optional.of(new BigDecimal("1.80")));

            MatchBetSnapshotDto result = betSnapshotService.getBetSnapshot(matchId, OddsType.HOME_WIN);

            assertThat(result.matchId()).isEqualTo(matchId);
            assertThat(result.homeTeamName()).isEqualTo("Home FC");
            assertThat(result.awayTeamName()).isEqualTo("Away FC");
            assertThat(result.odd()).isEqualByComparingTo("1.80");
            assertThat(result.matchStartTime()).isEqualTo(pool.getScheduledStartTime());
        }

        @Test
        @DisplayName("Should throw when match is LIVE")
        void should_throwException_when_matchIsLive() {
            Match match = buildMatch(MatchStatus.LIVE);
            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

            assertThatThrownBy(() -> betSnapshotService.getBetSnapshot(matchId, OddsType.HOME_WIN))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(matchId.toString());
        }

        @Test
        @DisplayName("Should throw when match is FINISHED")
        void should_throwException_when_matchIsFinished() {
            Match match = buildMatch(MatchStatus.FINISHED);
            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

            assertThatThrownBy(() -> betSnapshotService.getBetSnapshot(matchId, OddsType.AWAY_WIN))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(matchId.toString());
        }

        @Test
        @DisplayName("Should throw when match does not exist")
        void should_throwException_when_matchNotFound() {
            when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> betSnapshotService.getBetSnapshot(matchId, OddsType.DRAW))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Should fall back to odds 1.0 when no odds stored")
        void should_fallbackToOneOdd_when_oddsNotFound() {
            Match match = buildMatch(MatchStatus.SCHEDULED);
            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
            when(teamService.getStatsByIds(anyList())).thenReturn(List.of(
                    teamStats(homeTeamId, "Home FC"),
                    teamStats(awayTeamId, "Away FC")
            ));
            when(oddsService.getOdds(matchId, OddsType.DRAW)).thenReturn(Optional.empty());

            MatchBetSnapshotDto result = betSnapshotService.getBetSnapshot(matchId, OddsType.DRAW);

            assertThat(result.odd()).isEqualByComparingTo(BigDecimal.ONE);
        }
    }

    @Nested
    @DisplayName("getBetSnapshots — batch")
    class GetBetSnapshotsBatch {

        @Test
        @DisplayName("Should return snapshots when all matches are SCHEDULED")
        void should_returnSnapshots_when_allMatchesScheduled() {
            UUID matchId2 = UUID.randomUUID();
            UUID homeTeamId2 = UUID.randomUUID();
            UUID awayTeamId2 = UUID.randomUUID();

            Match match1 = buildMatch(MatchStatus.SCHEDULED);
            Match match2 = Match.builder()
                    .id(matchId2)
                    .homeTeamId(homeTeamId2)
                    .awayTeamId(awayTeamId2)
                    .status(MatchStatus.SCHEDULED)
                    .build();
            match2.setPool(pool);

            when(matchRepository.findAllByIdWithPool(List.of(matchId, matchId2)))
                    .thenReturn(List.of(match1, match2));
            when(teamService.getStatsByIds(anyList())).thenReturn(List.of(
                    teamStats(homeTeamId, "Home FC"),
                    teamStats(awayTeamId, "Away FC"),
                    teamStats(homeTeamId2, "Home2 FC"),
                    teamStats(awayTeamId2, "Away2 FC")
            ));
            when(oddsService.getOdds(any(), any())).thenReturn(Optional.of(new BigDecimal("2.00")));

            List<BetSnapshotRequest> requests = List.of(
                    new BetSnapshotRequest(matchId, OddsType.HOME_WIN),
                    new BetSnapshotRequest(matchId2, OddsType.AWAY_WIN)
            );

            List<MatchBetSnapshotDto> result = betSnapshotService.getBetSnapshots(requests);

            assertThat(result).hasSize(2);
            assertThat(result).extracting(MatchBetSnapshotDto::matchId)
                    .containsExactlyInAnyOrder(matchId, matchId2);
        }

        @Test
        @DisplayName("Should throw when any match in batch is LIVE")
        void should_throwException_when_anyMatchInBatchIsLive() {
            Match liveMatch = buildMatch(MatchStatus.LIVE);
            when(matchRepository.findAllByIdWithPool(List.of(matchId)))
                    .thenReturn(List.of(liveMatch));
            when(teamService.getStatsByIds(anyList())).thenReturn(List.of());

            List<BetSnapshotRequest> requests = List.of(new BetSnapshotRequest(matchId, OddsType.HOME_WIN));

            assertThatThrownBy(() -> betSnapshotService.getBetSnapshots(requests))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(matchId.toString());
        }
    }
}
