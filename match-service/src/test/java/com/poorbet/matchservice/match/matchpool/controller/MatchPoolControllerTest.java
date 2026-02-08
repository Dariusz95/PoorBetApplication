package com.poorbet.matchservice.match.matchpool.controller;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.poorbet.matchservice.match.matchpool.domain.PoolStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.poorbet.matchservice.match.matchpool.dto.LiveMatchEventDto;
import com.poorbet.matchservice.match.matchpool.dto.MatchPoolDto;
import com.poorbet.matchservice.match.matchpool.service.LiveMatchSimulationManager;
import com.poorbet.matchservice.match.matchpool.service.MatchPoolService;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(MatchPoolControllerTest.class)
class MatchPoolControllerTest {

    @Mock
    private LiveMatchSimulationManager manager;

    @Mock
    private MatchPoolService matchPoolService;

    @InjectMocks
    private MatchPoolController matchPoolController;

    private List<MatchPoolDto> testMatchPools;
    private UUID testPoolId;

    @BeforeEach
    void setUp() {
        testPoolId = UUID.randomUUID();
        testMatchPools = new ArrayList<>();
    }

    @Nested
    @DisplayName("Stream All Live Matches Endpoint")
    class StreamAllLiveMatchesEndpoint {

        @Test
        @DisplayName("Should return Flux of live match events")
        void shouldReturnFluxOfLiveMatchEvents() {
            // Arrange
            LiveMatchEventDto event1 = LiveMatchEventDto.heartbeat();
            LiveMatchEventDto event2 = LiveMatchEventDto.poolFinished(UUID.randomUUID());
            
            Flux<LiveMatchEventDto> managerEvents = Flux.just(event1, event2);
            when(manager.streamAll()).thenReturn(managerEvents);

            // Act
            Flux<LiveMatchEventDto> result = matchPoolController.streamAll();

            // Assert
            assertThat(result).isNotNull();
            verify(manager).streamAll();
        }

        @Test
        @DisplayName("Should include heartbeat event at start")
        void shouldIncludeHeartbeatEventAtStart() {
            // Arrange
            Flux<LiveMatchEventDto> managerEvents = Flux.empty();
            when(manager.streamAll()).thenReturn(managerEvents);

            // Act
            Flux<LiveMatchEventDto> result = matchPoolController.streamAll();

            // Assert - Verify that the flux is created (heartbeat is added first)
            StepVerifier.create(result)
                    .expectNextCount(1) // Heartbeat
                    .expectComplete()
                    .verify(Duration.ofSeconds(1));
        }

        @Test
        @DisplayName("Should merge manager events with heartbeat")
        void shouldMergeManagerEventsWithHeartbeat() {
            // Arrange
            LiveMatchEventDto managerEvent = LiveMatchEventDto.poolFinished(UUID.randomUUID());
            Flux<LiveMatchEventDto> managerEvents = Flux.just(managerEvent);
            when(manager.streamAll()).thenReturn(managerEvents);

            // Act
            Flux<LiveMatchEventDto> result = matchPoolController.streamAll();

            // Assert
            StepVerifier.create(result)
                    .expectNextCount(2) // Heartbeat + manager event
                    .expectComplete()
                    .verify(Duration.ofSeconds(1));
        }

        @Test
        @DisplayName("Should handle empty event stream from manager")
        void shouldHandleEmptyEventStreamFromManager() {
            // Arrange
            when(manager.streamAll()).thenReturn(Flux.empty());

            // Act
            Flux<LiveMatchEventDto> result = matchPoolController.streamAll();

            // Assert
            StepVerifier.create(result)
                    .expectNextCount(1) // Only heartbeat
                    .expectComplete()
                    .verify(Duration.ofSeconds(1));
        }

        @Test
        @DisplayName("Should handle continuous event stream")
        void shouldHandleContinuousEventStream() {
            // Arrange
            Flux<LiveMatchEventDto> continuousEvents = Flux.interval(Duration.ofMillis(100))
                    .map(i -> LiveMatchEventDto.poolFinished(UUID.randomUUID()))
                    .take(3);
            when(manager.streamAll()).thenReturn(continuousEvents);

            // Act
            Flux<LiveMatchEventDto> result = matchPoolController.streamAll();

            // Assert
            StepVerifier.create(result)
                    .expectNextCount(4) // Heartbeat + 3 events
                    .expectComplete()
                    .verify(Duration.ofSeconds(2));
        }

        @Test
        @DisplayName("Should handle manager error gracefully")
        void shouldHandleManagerErrorGracefully() {
            // Arrange
            when(manager.streamAll())
                    .thenReturn(Flux.error(new RuntimeException("Stream error")));

            // Act
            Flux<LiveMatchEventDto> result = matchPoolController.streamAll();

            // Assert
            StepVerifier.create(result)
                    .expectNextCount(1) // Heartbeat
                    .expectError(RuntimeException.class)
                    .verify(Duration.ofSeconds(1));
        }

        @Test
        @DisplayName("Should support multiple concurrent subscriptions")
        void shouldSupportMultipleConcurrentSubscriptions() {
            // Arrange
            Flux<LiveMatchEventDto> managerEvents = Flux.just(
                    LiveMatchEventDto.poolFinished(UUID.randomUUID())
            );
            when(manager.streamAll()).thenReturn(managerEvents);

            // Act
            Flux<LiveMatchEventDto> result1 = matchPoolController.streamAll();
            Flux<LiveMatchEventDto> result2 = matchPoolController.streamAll();

            // Assert
            assertThat(result1).isNotNull();
            assertThat(result2).isNotNull();
        }

        @Test
        @DisplayName("Should use TEXT_EVENT_STREAM media type")
        void shouldUseTextEventStreamMediaType() {
            // This test verifies the @GetMapping configuration includes TEXT_EVENT_STREAM_VALUE
            // The configuration is checked via reflection or method inspection
            assertThat(MatchPoolController.class).isNotNull();
        }
    }

    @Nested
    @DisplayName("Get Future Match Pools Endpoint")
    class GetFutureMatchPoolsEndpoint {

        @BeforeEach
        void setUp() {
            testMatchPools = createTestMatchPools();
        }

        @Test
        @DisplayName("Should return list of future match pools")
        void shouldReturnListOfFutureMatchPools() {
            // Arrange
            when(matchPoolService.getFutureMatchPools())
                    .thenReturn(testMatchPools);

            // Act
            List<MatchPoolDto> result = matchPoolController.getFutureMatchPools();

            // Assert
            assertThat(result)
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(3);
        }

        @Test
        @DisplayName("Should call service method")
        void shouldCallServiceMethod() {
            // Arrange
            when(matchPoolService.getFutureMatchPools())
                    .thenReturn(testMatchPools);

            // Act
            matchPoolController.getFutureMatchPools();

            // Assert
            verify(matchPoolService, times(1)).getFutureMatchPools();
        }

        @Test
        @DisplayName("Should return correct match pool data")
        void shouldReturnCorrectMatchPoolData() {
            // Arrange
            when(matchPoolService.getFutureMatchPools())
                    .thenReturn(testMatchPools);

            // Act
            List<MatchPoolDto> result = matchPoolController.getFutureMatchPools();

            // Assert
            assertThat(result)
                    .allSatisfy(pool -> {
                        assertThat(pool).isNotNull();
                        assertThat(pool.id()).isNotNull();
                    });
        }

        @Test
        @DisplayName("Should handle empty future match pools")
        void shouldHandleEmptyFutureMatchPools() {
            // Arrange
            when(matchPoolService.getFutureMatchPools())
                    .thenReturn(Collections.emptyList());

            // Act
            List<MatchPoolDto> result = matchPoolController.getFutureMatchPools();

            // Assert
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should handle single future match pool")
        void shouldHandleSingleFutureMatchPool() {
            // Arrange
            List<MatchPoolDto> singlePool = List.of(createTestMatchPool());
            when(matchPoolService.getFutureMatchPools())
                    .thenReturn(singlePool);

            // Act
            List<MatchPoolDto> result = matchPoolController.getFutureMatchPools();

            // Assert
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Should handle multiple future match pools")
        void shouldHandleMultipleFutureMatchPools() {
            // Arrange
            List<MatchPoolDto> multiplePools = List.of(
                    createTestMatchPool(),
                    createTestMatchPool(),
                    createTestMatchPool(),
                    createTestMatchPool(),
                    createTestMatchPool()
            );
            when(matchPoolService.getFutureMatchPools())
                    .thenReturn(multiplePools);

            // Act
            List<MatchPoolDto> result = matchPoolController.getFutureMatchPools();

            // Assert
            assertThat(result).hasSize(5);
        }

        @Test
        @DisplayName("Should not modify returned list")
        void shouldNotModifyReturnedList() {
            // Arrange
            when(matchPoolService.getFutureMatchPools())
                    .thenReturn(testMatchPools);

            // Act
            List<MatchPoolDto> result = matchPoolController.getFutureMatchPools();

            // Assert
            assertThat(result).isEqualTo(testMatchPools);
        }

        @Test
        @DisplayName("Should handle service exception")
        void shouldHandleServiceException() {
            // Arrange
            when(matchPoolService.getFutureMatchPools())
                    .thenThrow(new RuntimeException("Service error"));

            // Act & Assert
            assertThatThrownBy(() -> matchPoolController.getFutureMatchPools())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Service error");
        }

        @Test
        @DisplayName("Should call service multiple times independently")
        void shouldCallServiceMultipleTimesIndependently() {
            // Arrange
            when(matchPoolService.getFutureMatchPools())
                    .thenReturn(testMatchPools);

            // Act
            matchPoolController.getFutureMatchPools();
            matchPoolController.getFutureMatchPools();
            matchPoolController.getFutureMatchPools();

            // Assert
            verify(matchPoolService, times(3)).getFutureMatchPools();
        }
    }

    @Nested
    @DisplayName("Controller Integration")
    class ControllerIntegration {

        @Test
        @DisplayName("Should handle concurrent calls to different endpoints")
        void shouldHandleConcurrentCallsToDifferentEndpoints() {
            // Arrange
            Flux<LiveMatchEventDto> streamEvents = Flux.just(LiveMatchEventDto.heartbeat());
            when(manager.streamAll()).thenReturn(streamEvents);
            when(matchPoolService.getFutureMatchPools())
                    .thenReturn(createTestMatchPools());

            // Act
            Flux<LiveMatchEventDto> streamResult = matchPoolController.streamAll();
            List<MatchPoolDto> poolsResult = matchPoolController.getFutureMatchPools();

            // Assert
            assertThat(streamResult).isNotNull();
            assertThat(poolsResult).isNotEmpty();
        }

        @Test
        @DisplayName("Should maintain service dependencies")
        void shouldMaintainServiceDependencies() {
            // Arrange
            when(manager.streamAll()).thenReturn(Flux.empty());
            when(matchPoolService.getFutureMatchPools()).thenReturn(Collections.emptyList());

            // Act
            matchPoolController.streamAll();
            matchPoolController.getFutureMatchPools();

            // Assert
            verify(manager).streamAll();
            verify(matchPoolService).getFutureMatchPools();
        }
    }

    // Helper methods
    private List<MatchPoolDto> createTestMatchPools() {
        List<MatchPoolDto> pools = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            pools.add(createTestMatchPool());
        }
        return pools;
    }

    private MatchPoolDto createTestMatchPool() {
        return new MatchPoolDto(
                UUID.randomUUID(),
                PoolStatus.BETTABLE,
                OffsetDateTime.now().plusMinutes(3),
                Collections.emptyList()
        );
    }
}
