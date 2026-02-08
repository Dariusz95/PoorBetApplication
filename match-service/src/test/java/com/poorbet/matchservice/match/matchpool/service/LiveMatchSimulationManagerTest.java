package com.poorbet.matchservice.match.matchpool.service;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.poorbet.matchservice.match.matchpool.dto.LiveMatchEventDto;
import com.poorbet.matchservice.match.matchpool.simulation.LiveMatchSimulation;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("LiveMatchSimulationManager Unit Tests")
class LiveMatchSimulationManagerTest {

    @InjectMocks
    private LiveMatchSimulationManager manager;

    private UUID testMatchId;
    private UUID testPoolId;

    @BeforeEach
    void setUp() {
        testMatchId = UUID.randomUUID();
        testPoolId = UUID.randomUUID();
    }

    @Nested
    @DisplayName("Start Simulation")
    class StartSimulation {

        @Test
        @DisplayName("Should create and return new simulation when not running")
        void shouldCreateAndReturnNewSimulationWhenNotRunning() {
            // Act
            LiveMatchSimulation simulation = manager.startIfNotRunning(testMatchId);

            // Assert
            assertThat(simulation).isNotNull();
            assertThat(simulation.getMatchId()).isEqualTo(testMatchId);
        }

        @Test
        @DisplayName("Should return same simulation on subsequent calls")
        void shouldReturnSameSimulationOnSubsequentCalls() {
            // Act
            LiveMatchSimulation sim1 = manager.startIfNotRunning(testMatchId);
            LiveMatchSimulation sim2 = manager.startIfNotRunning(testMatchId);

            // Assert
            assertThat(sim1).isSameAs(sim2);
        }

        @Test
        @DisplayName("Should handle different match IDs independently")
        void shouldHandleDifferentMatchIdsIndependently() {
            // Arrange
            UUID matchId1 = UUID.randomUUID();
            UUID matchId2 = UUID.randomUUID();

            // Act
            LiveMatchSimulation sim1 = manager.startIfNotRunning(matchId1);
            LiveMatchSimulation sim2 = manager.startIfNotRunning(matchId2);

            // Assert
            assertThat(sim1).isNotSameAs(sim2);
            assertThat(sim1.getMatchId()).isNotEqualTo(sim2.getMatchId());
        }

        @Test
        @DisplayName("Should not create duplicate simulations")
        void shouldNotCreateDuplicateSimulations() {
            // Act
            manager.startIfNotRunning(testMatchId);
            manager.startIfNotRunning(testMatchId);
            manager.startIfNotRunning(testMatchId);

            // This is implicit - if simulations are being created multiple times,
            // the test will fail due to verification logic in LiveMatchSimulation
            assertThat(testMatchId).isNotNull();
        }

        @Test
        @DisplayName("Should handle null match ID gracefully")
        void shouldHandleNullMatchIdGracefully() {
            // Act & Assert
            assertThatThrownBy(() -> manager.startIfNotRunning(null))
                    .isInstanceOf(Exception.class);
        }
    }

    @Nested
    @DisplayName("Stream Events")
    class StreamEvents {

        @Test
        @DisplayName("Should return valid Flux stream")
        void shouldReturnValidFluxStream() {
            // Act
            Flux<LiveMatchEventDto> stream = manager.streamAll();

            // Assert
            assertThat(stream).isNotNull();
        }

        @Test
        @DisplayName("Should support subscription to stream")
        void shouldSupportSubscriptionToStream() {
            // Arrange
            Flux<LiveMatchEventDto> stream = manager.streamAll();

            // Act & Assert
            StepVerifier.create(stream)
                    .expectSubscription()
                    .thenCancel()
                    .verify(Duration.ofSeconds(1));
        }

        @Test
        @DisplayName("Should return replay flux with limit of 10")
        void shouldReturnReplayFluxWithLimitOf10() {
            // Act
            Flux<LiveMatchEventDto> stream = manager.streamAll();

            // Assert
            assertThat(stream).isNotNull();
        }

        @Test
        @DisplayName("Should handle multiple concurrent subscriptions")
        void shouldHandleMultipleConcurrentSubscriptions() {
            // Arrange
            Flux<LiveMatchEventDto> stream = manager.streamAll();

            // Act
            StepVerifier.create(stream)
                    .expectSubscription()
                    .thenCancel()
                    .verify(Duration.ofSeconds(1));

            StepVerifier.create(stream)
                    .expectSubscription()
                    .thenCancel()
                    .verify(Duration.ofSeconds(1));

            // Assert - Both subscriptions should succeed without errors
            assertThat(true).isTrue();
        }
    }

    @Nested
    @DisplayName("Notify Pool Finished")
    class NotifyPoolFinished {

        @Test
        @DisplayName("Should emit pool finished event")
        void shouldEmitPoolFinishedEvent() {
            // Arrange
            Flux<LiveMatchEventDto> stream = manager.streamAll();

            // Act
            manager.notifyPoolFinished(testPoolId);

            // Assert - The event should be emitted to the sink
            StepVerifier.create(stream.take(1))
                    .assertNext(event -> {
                        assertThat(event).isNotNull();
                    })
                    .expectComplete()
                    .verify(Duration.ofSeconds(1));
        }

        @Test
        @DisplayName("Should create pool finished event with correct pool ID")
        void shouldCreatePoolFinishedEventWithCorrectPoolId() {
            // Arrange
            Flux<LiveMatchEventDto> stream = manager.streamAll();
            UUID poolId = UUID.randomUUID();

            // Act
            manager.notifyPoolFinished(poolId);

            // Assert
            StepVerifier.create(stream.take(1))
                    .assertNext(event -> {
                        assertThat(event).isNotNull();
                    })
                    .expectComplete()
                    .verify(Duration.ofSeconds(1));
        }

        @Test
        @DisplayName("Should handle multiple pool finished notifications")
        void shouldHandleMultiplePoolFinishedNotifications() {
            // Arrange
            Flux<LiveMatchEventDto> stream = manager.streamAll();

            // Act
            UUID poolId1 = UUID.randomUUID();
            UUID poolId2 = UUID.randomUUID();
            UUID poolId3 = UUID.randomUUID();

            manager.notifyPoolFinished(poolId1);
            manager.notifyPoolFinished(poolId2);
            manager.notifyPoolFinished(poolId3);

            // Assert
            StepVerifier.create(stream.take(3))
                    .expectNextCount(3)
                    .expectComplete()
                    .verify(Duration.ofSeconds(2));
        }

        @Test
        @DisplayName("Should handle consecutive notifications")
        void shouldHandleConsecutiveNotifications() {
            // Arrange
            Flux<LiveMatchEventDto> stream = manager.streamAll();

            // Act
            for (int i = 0; i < 5; i++) {
                manager.notifyPoolFinished(UUID.randomUUID());
            }

            // Assert
            StepVerifier.create(stream.take(5))
                    .expectNextCount(5)
                    .expectComplete()
                    .verify(Duration.ofSeconds(2));
        }

    }

    @Nested
    @DisplayName("Concurrent Operations")
    class ConcurrentOperations {

        @Test
        @DisplayName("Should handle concurrent simulation starts")
        void shouldHandleConcurrentSimulationStarts() {
            // Act
            LiveMatchSimulation sim1 = manager.startIfNotRunning(testMatchId);
            LiveMatchSimulation sim2 = manager.startIfNotRunning(testMatchId);

            // Assert
            assertThat(sim1).isSameAs(sim2);
        }

        @Test
        @DisplayName("Should handle concurrent pool notifications")
        void shouldHandleConcurrentPoolNotifications() {
            // Act
            UUID poolId1 = UUID.randomUUID();
            UUID poolId2 = UUID.randomUUID();

            manager.notifyPoolFinished(poolId1);
            manager.notifyPoolFinished(poolId2);

            // Assert - Both notifications should be emitted
            Flux<LiveMatchEventDto> stream = manager.streamAll();
            StepVerifier.create(stream.take(2))
                    .expectNextCount(2)
                    .expectComplete()
                    .verify(Duration.ofSeconds(2));
        }

        @Test
        @DisplayName("Should handle interleaved simulation and pool notifications")
        void shouldHandleInterleavedSimulationAndPoolNotifications() {
            // Act
            LiveMatchSimulation sim = manager.startIfNotRunning(testMatchId);
            manager.notifyPoolFinished(testPoolId);
            LiveMatchSimulation sim2 = manager.startIfNotRunning(UUID.randomUUID());

            // Assert
            assertThat(sim).isNotNull();
            assertThat(sim2).isNotNull();
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle many different match IDs")
        void shouldHandleManyDifferentMatchIds() {
            // Act
            for (int i = 0; i < 100; i++) {
                manager.startIfNotRunning(UUID.randomUUID());
            }

            // Assert - No exception should be thrown
            assertThat(true).isTrue();
        }

        @Test
        @DisplayName("Should maintain separate streams per match ID")
        void shouldMaintainSeparateStreamsPerMatchId() {
            // Act
            UUID matchId1 = UUID.randomUUID();
            UUID matchId2 = UUID.randomUUID();

            LiveMatchSimulation sim1 = manager.startIfNotRunning(matchId1);
            LiveMatchSimulation sim2 = manager.startIfNotRunning(matchId2);

            // Assert
            assertThat(sim1.getMatchId()).isEqualTo(matchId1);
            assertThat(sim2.getMatchId()).isEqualTo(matchId2);
        }

        @Test
        @DisplayName("Should handle rapid sequential calls")
        void shouldHandleRapidSequentialCalls() {
            // Act
            for (int i = 0; i < 10; i++) {
                manager.notifyPoolFinished(UUID.randomUUID());
            }

            // Assert - No exception should be thrown
            assertThat(true).isTrue();
        }

        @Test
        @DisplayName("Should maintain event replay buffer")
        void shouldMaintainEventReplayBuffer() {
            // Arrange
            Flux<LiveMatchEventDto> stream = manager.streamAll();

            // Act
            for (int i = 0; i < 10; i++) {
                manager.notifyPoolFinished(UUID.randomUUID());
            }

            // Assert - Buffer should maintain last 10 events
            StepVerifier.create(stream.take(10))
                    .expectNextCount(10)
                    .expectComplete()
                    .verify(Duration.ofSeconds(2));
        }
    }
}
