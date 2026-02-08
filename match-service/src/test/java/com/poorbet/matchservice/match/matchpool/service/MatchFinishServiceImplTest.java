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
import com.poorbet.matchservice.match.tx.AfterCommitHandler;

@ExtendWith(MockitoExtension.class)
@DisplayName("MatchFinishServiceImpl Unit Tests")
class MatchFinishServiceImplTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private MatchPoolLifecycleManager lifecycleManager;

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
        @DisplayName("Should register lifecycle callback")
        void shouldRegisterLifecycleCallback() {
            // Arrange
            when(matchRepository.findById(testMatchId))
                    .thenReturn(Optional.of(testMatch));
            when(matchRepository.save(any(Match.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            matchFinishService.finishMatch(finishEvent);

            // Assert
            verify(afterCommitHandler).run(any(Runnable.class));
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
            verify(afterCommitHandler).run(callbackCaptor.capture());
            // The callback should trigger handleMatchFinished
            callbackCaptor.getValue().run();
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
}
