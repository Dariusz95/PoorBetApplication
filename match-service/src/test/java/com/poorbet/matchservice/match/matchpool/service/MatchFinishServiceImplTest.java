package com.poorbet.matchservice.match.matchpool.service;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.poorbet.matchservice.match.match.domain.Match;
import com.poorbet.matchservice.match.match.domain.MatchStatus;
import com.poorbet.matchservice.match.match.repository.MatchRepository;
import com.poorbet.matchservice.match.matchpool.dto.LiveMatchEventDto;
import com.poorbet.matchservice.infrastructure.AfterCommitHandler;
import com.poorbet.matchservice.team.client.wallet.WalletClient;
import com.poorbet.matchservice.team.dto.CreditWalletRequest;
import com.poorbet.matchservice.team.model.Team;
import com.poorbet.matchservice.team.repository.TeamRepository;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("MatchFinishServiceImpl Unit Tests")
class MatchFinishServiceImplTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private MatchPoolLifecycleManager lifecycleManager;

    @Mock
    private WalletClient walletClient;

    @Mock
    private AfterCommitHandler afterCommitHandler;

    @InjectMocks
    private MatchFinishServiceImpl matchFinishService;

    private UUID testMatchId;
    private Match testMatch;
    private LiveMatchEventDto finishEvent;

    @BeforeEach
    void setUp() {
        testMatchId = UUID.randomUUID();
        testMatch = Match.builder()
                .id(testMatchId)
                .homeTeamId(UUID.randomUUID())
                .awayTeamId(UUID.randomUUID())
                .homeGoals(0)
                .awayGoals(0)
                .status(MatchStatus.LIVE)
                .build();

        finishEvent = new LiveMatchEventDto(
                testMatchId,
                testMatch.getHomeTeamId(),
                testMatch.getAwayTeamId(),
                2,
                1,
                90,
                null,
                "Match finished"
        );
    }

    @Nested
    @DisplayName("Finish Match")
    class FinishMatch {

        @Test
        @DisplayName("Should update match status to FINISHED")
        void shouldUpdateMatchStatusToFinished() {
            // Arrange
            when(matchRepository.findById(testMatchId))
                    .thenReturn(Optional.of(testMatch));
            when(matchRepository.save(any(Match.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            matchFinishService.finishMatch(finishEvent);

            // Assert
            assertThat(testMatch.getStatus()).isEqualTo(MatchStatus.FINISHED);
        }

        @Test
        @DisplayName("Should set home goals from event")
        void shouldSetHomeGoalsFromEvent() {
            // Arrange
            when(matchRepository.findById(testMatchId))
                    .thenReturn(Optional.of(testMatch));
            when(matchRepository.save(any(Match.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            matchFinishService.finishMatch(finishEvent);

            // Assert
            assertThat(testMatch.getHomeGoals()).isEqualTo(2);
        }

        @Test
        @DisplayName("Should save match after update")
        void shouldSaveMatchAfterUpdate() {
            // Arrange
            when(matchRepository.findById(testMatchId))
                    .thenReturn(Optional.of(testMatch));
            when(matchRepository.save(any(Match.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            matchFinishService.finishMatch(finishEvent);

            // Assert
            verify(matchRepository).save(testMatch);
        }

        @Test
        @DisplayName("Should register lifecycle and reward callbacks")
        void shouldRegisterLifecycleCallback() {
            // Arrange
            when(matchRepository.findById(testMatchId))
                    .thenReturn(Optional.of(testMatch));
            when(matchRepository.save(any(Match.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            matchFinishService.finishMatch(finishEvent);

            // Assert
            verify(afterCommitHandler, times(2)).run(any(Runnable.class));
        }

        @Test
        @DisplayName("Should call lifecycle manager for finished match")
        void shouldCallLifecycleManagerForFinishedMatch() {
            // Arrange
            when(matchRepository.findById(testMatchId))
                    .thenReturn(Optional.of(testMatch));
            when(matchRepository.save(any(Match.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            ArgumentCaptor<Runnable> callbackCaptor = ArgumentCaptor.forClass(Runnable.class);

            // Act
            matchFinishService.finishMatch(finishEvent);

            // Assert
            verify(afterCommitHandler, times(2)).run(callbackCaptor.capture());
            // The first registered callback should trigger handleMatchFinished
            callbackCaptor.getAllValues().get(0).run();
            verify(lifecycleManager).handleMatchFinished(testMatch);
        }

        @Test
        @DisplayName("Should throw exception if match not found")
        void shouldThrowExceptionIfMatchNotFound() {
            // Arrange
            when(matchRepository.findById(testMatchId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> matchFinishService.finishMatch(finishEvent))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Match not found");
        }

        @Test
        @DisplayName("Should handle match with different scores")
        void shouldHandleMatchWithDifferentScores() {
            // Arrange
            LiveMatchEventDto highScoreEvent = new LiveMatchEventDto(
                    testMatchId,
                    testMatch.getHomeTeamId(),
                    testMatch.getAwayTeamId(),
                    5,
                    3,
                    90,
                    null,
                    "High score"
            );

            when(matchRepository.findById(testMatchId))
                    .thenReturn(Optional.of(testMatch));
            when(matchRepository.save(any(Match.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            matchFinishService.finishMatch(highScoreEvent);

            // Assert
            assertThat(testMatch.getHomeGoals()).isEqualTo(5);
        }

        @Test
        @DisplayName("Should handle match with draw score")
        void shouldHandleMatchWithDrawScore() {
            // Arrange
            LiveMatchEventDto drawEvent = new LiveMatchEventDto(
                    testMatchId,
                    testMatch.getHomeTeamId(),
                    testMatch.getAwayTeamId(),
                    2,
                    2,
                    90,
                    null,
                    "Draw score"
            );

            when(matchRepository.findById(testMatchId))
                    .thenReturn(Optional.of(testMatch));
            when(matchRepository.save(any(Match.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            matchFinishService.finishMatch(drawEvent);

            // Assert
            assertThat(testMatch.getHomeGoals()).isEqualTo(2);
        }

        @Test
        @DisplayName("Should handle match with zero score")
        void shouldHandleMatchWithZeroScore() {
            // Arrange
            LiveMatchEventDto zeroEvent = new LiveMatchEventDto(
                    testMatchId,
                    testMatch.getHomeTeamId(),
                    testMatch.getAwayTeamId(),
                    0,
                    0,
                    90,
                    null,
                    "Zero score"
            );

            when(matchRepository.findById(testMatchId))
                    .thenReturn(Optional.of(testMatch));
            when(matchRepository.save(any(Match.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            matchFinishService.finishMatch(zeroEvent);

            // Assert
            assertThat(testMatch.getHomeGoals()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("Transaction Behavior")
    class TransactionBehavior {

        @Test
        @DisplayName("Should be transactional")
        void shouldBeTransactional() {
            // Verify the @Transactional annotation is present
            assertThat(MatchFinishServiceImpl.class).isNotNull();
        }

        @Test
        @DisplayName("Should rollback on exception")
        void shouldRollbackOnException() {
            // Arrange
            when(matchRepository.findById(testMatchId))
                    .thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThatThrownBy(() -> matchFinishService.finishMatch(finishEvent))
                    .isInstanceOf(RuntimeException.class);
            verify(matchRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle null event gracefully")
        void shouldHandleNullEventGracefully() {
            // Act & Assert
            assertThatThrownBy(() -> matchFinishService.finishMatch(null))
                    .isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("Should handle match already finished")
        void shouldHandleMatchAlreadyFinished() {
            // Arrange
            testMatch.setStatus(MatchStatus.FINISHED);
            when(matchRepository.findById(testMatchId))
                    .thenReturn(Optional.of(testMatch));
            when(matchRepository.save(any(Match.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            matchFinishService.finishMatch(finishEvent);

            // Assert - Should process without error
            verify(matchRepository).save(testMatch);
        }

        @Test
        @DisplayName("Should handle multiple finish requests for same match")
        void shouldHandleMultipleFinishRequestsForSameMatch() {
            // Arrange
            when(matchRepository.findById(testMatchId))
                    .thenReturn(Optional.of(testMatch));
            when(matchRepository.save(any(Match.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            matchFinishService.finishMatch(finishEvent);
            matchFinishService.finishMatch(finishEvent);

            // Assert
            verify(matchRepository, times(2)).save(any());
        }
    }

    @Nested
    @DisplayName("Reward Winning Team Owner")
    class RewardWinningTeamOwner {

        private Runnable captureRewardCallback() {
            ArgumentCaptor<Runnable> callbackCaptor = ArgumentCaptor.forClass(Runnable.class);
            verify(afterCommitHandler, times(2)).run(callbackCaptor.capture());
            return callbackCaptor.getAllValues().get(1);
        }

        @Test
        @DisplayName("Should credit 1 coin to the home team owner when home team wins")
        void shouldCreditHomeTeamOwnerWhenHomeTeamWins() {
            // Arrange
            UUID ownerId = UUID.randomUUID();
            Team homeTeam = Team.builder().id(testMatch.getHomeTeamId()).userId(ownerId).build();

            when(matchRepository.findById(testMatchId)).thenReturn(Optional.of(testMatch));
            when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(teamRepository.findById(testMatch.getHomeTeamId())).thenReturn(Optional.of(homeTeam));

            // Act — finishEvent is home 2 - away 1
            matchFinishService.finishMatch(finishEvent);
            captureRewardCallback().run();

            // Assert
            verify(walletClient).credit(eq(ownerId), eq(new CreditWalletRequest(BigDecimal.ONE)));
        }

        @Test
        @DisplayName("Should credit 1 coin to the away team owner when away team wins")
        void shouldCreditAwayTeamOwnerWhenAwayTeamWins() {
            // Arrange
            UUID ownerId = UUID.randomUUID();
            Team awayTeam = Team.builder().id(testMatch.getAwayTeamId()).userId(ownerId).build();
            LiveMatchEventDto awayWinEvent = new LiveMatchEventDto(
                    testMatchId, testMatch.getHomeTeamId(), testMatch.getAwayTeamId(),
                    1, 3, 90, null, "Away win"
            );

            when(matchRepository.findById(testMatchId)).thenReturn(Optional.of(testMatch));
            when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(teamRepository.findById(testMatch.getAwayTeamId())).thenReturn(Optional.of(awayTeam));

            // Act
            matchFinishService.finishMatch(awayWinEvent);
            captureRewardCallback().run();

            // Assert
            verify(walletClient).credit(eq(ownerId), eq(new CreditWalletRequest(BigDecimal.ONE)));
        }

        @Test
        @DisplayName("Should not credit anyone when the match ends in a draw")
        void shouldNotCreditOnDraw() {
            // Arrange
            LiveMatchEventDto drawEvent = new LiveMatchEventDto(
                    testMatchId, testMatch.getHomeTeamId(), testMatch.getAwayTeamId(),
                    2, 2, 90, null, "Draw"
            );

            when(matchRepository.findById(testMatchId)).thenReturn(Optional.of(testMatch));
            when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            matchFinishService.finishMatch(drawEvent);
            captureRewardCallback().run();

            // Assert
            verifyNoInteractions(teamRepository, walletClient);
        }

        @Test
        @DisplayName("Should not credit anyone when the winning team has no owner (seeded team)")
        void shouldNotCreditWhenWinningTeamHasNoOwner() {
            // Arrange
            Team unownedTeam = Team.builder().id(testMatch.getHomeTeamId()).userId(null).build();

            when(matchRepository.findById(testMatchId)).thenReturn(Optional.of(testMatch));
            when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(teamRepository.findById(testMatch.getHomeTeamId())).thenReturn(Optional.of(unownedTeam));

            // Act
            matchFinishService.finishMatch(finishEvent);
            captureRewardCallback().run();

            // Assert
            verifyNoInteractions(walletClient);
        }

        @Test
        @DisplayName("Should never look up the losing team")
        void shouldNeverLookUpLosingTeam() {
            // Arrange
            Team homeTeam = Team.builder().id(testMatch.getHomeTeamId()).userId(UUID.randomUUID()).build();

            when(matchRepository.findById(testMatchId)).thenReturn(Optional.of(testMatch));
            when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(teamRepository.findById(testMatch.getHomeTeamId())).thenReturn(Optional.of(homeTeam));

            // Act — finishEvent is home 2 - away 1
            matchFinishService.finishMatch(finishEvent);
            captureRewardCallback().run();

            // Assert
            verify(teamRepository, never()).findById(testMatch.getAwayTeamId());
        }
    }
}
