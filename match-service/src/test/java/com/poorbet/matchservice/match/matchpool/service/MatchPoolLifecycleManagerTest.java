package com.poorbet.matchservice.match.matchpool.service;

import com.poorbet.commons.rabbit.events.match.MatchesFinishedEvent;
import com.poorbet.matchservice.infrastructure.outbox.OutboxService;
import com.poorbet.matchservice.match.match.domain.Match;
import com.poorbet.matchservice.match.match.domain.MatchStatus;
import com.poorbet.matchservice.match.match.dto.MatchResultDto;
import com.poorbet.matchservice.match.match.repository.MatchRepository;
import com.poorbet.matchservice.match.matchpool.domain.MatchPool;
import com.poorbet.matchservice.match.matchpool.domain.PoolStatus;
import com.poorbet.matchservice.match.matchpool.repository.MatchPoolRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MatchPoolLifecycleManager Unit Tests")
class MatchPoolLifecycleManagerTest {

    @Mock
    private MatchRepository matchRepository;
    @Mock
    private MatchPoolRepository matchPoolRepository;
    @Mock
    private LiveMatchSimulationManager liveMatchSimulationManager;
    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private OutboxService outboxService;

    @InjectMocks
    private MatchPoolLifecycleManager lifecycleManager;

    private UUID poolId;
    private Match match;

    @BeforeEach
    void setUp() {
        poolId = UUID.randomUUID();
        MatchPool pool = MatchPool.builder().id(poolId).build();
        match = Match.builder()
                .id(UUID.randomUUID())
                .pool(pool)
                .status(MatchStatus.FINISHED)
                .build();
    }

    private void stubRedisLock(Boolean acquired) {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(eq("match-pool:" + poolId + ":finalize"), eq("1"), eq(4L), eq(TimeUnit.MINUTES)))
                .thenReturn(acquired);
    }

    @Test
    @DisplayName("Should do nothing while other matches in the pool are still live")
    void shouldDoNothingWhileOtherMatchesAreLive() {
        // Arrange
        when(matchRepository.countByPoolIdAndStatus(poolId, MatchStatus.LIVE)).thenReturn(1L);

        // Act
        lifecycleManager.handleMatchFinished(match);

        // Assert
        verifyNoInteractions(redisTemplate, matchPoolRepository, outboxService, liveMatchSimulationManager);
    }

    @Test
    @DisplayName("Should finalize the pool, save the outbox event and notify simulation manager when this is the last live match")
    void shouldFinalizePoolWhenLastMatchFinishes() {
        // Arrange
        when(matchRepository.countByPoolIdAndStatus(poolId, MatchStatus.LIVE)).thenReturn(0L);
        stubRedisLock(true);
        when(matchPoolRepository.updateStatus(poolId, PoolStatus.FINISHED)).thenReturn(1);

        MatchResultDto resultDto = MatchResultDto.builder()
                .id(UUID.randomUUID())
                .homeGoals(2)
                .awayGoals(1)
                .status(MatchStatus.FINISHED)
                .build();
        when(matchPoolRepository.getResults(poolId)).thenReturn(List.of(resultDto));

        // Act
        lifecycleManager.handleMatchFinished(match);

        // Assert
        ArgumentCaptor<MatchesFinishedEvent> eventCaptor = ArgumentCaptor.forClass(MatchesFinishedEvent.class);
        verify(outboxService).saveEvent(any(), eventCaptor.capture());
        assertThat(eventCaptor.getValue().results()).hasSize(1);
        assertThat(eventCaptor.getValue().results().get(0).matchId()).isEqualTo(resultDto.getId());
        assertThat(eventCaptor.getValue().results().get(0).homeGoals()).isEqualTo(2);
        assertThat(eventCaptor.getValue().results().get(0).awayGoals()).isEqualTo(1);

        verify(liveMatchSimulationManager).notifyPoolFinished(poolId);
    }

    @Test
    @DisplayName("Should not finalize the pool twice when the Redis lock is already held")
    void shouldNotFinalizeWhenLockAlreadyHeld() {
        // Arrange
        when(matchRepository.countByPoolIdAndStatus(poolId, MatchStatus.LIVE)).thenReturn(0L);
        stubRedisLock(false);

        // Act
        lifecycleManager.handleMatchFinished(match);

        // Assert
        verify(matchPoolRepository, never()).updateStatus(any(), any());
        verifyNoInteractions(outboxService, liveMatchSimulationManager);
    }

    @Test
    @DisplayName("Should throw when the pool referenced by the match no longer exists")
    void shouldThrowWhenPoolNotFound() {
        // Arrange
        when(matchRepository.countByPoolIdAndStatus(poolId, MatchStatus.LIVE)).thenReturn(0L);
        stubRedisLock(true);
        when(matchPoolRepository.updateStatus(poolId, PoolStatus.FINISHED)).thenReturn(0);

        // Act & Assert
        assertThatThrownBy(() -> lifecycleManager.handleMatchFinished(match))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(poolId.toString());

        verifyNoInteractions(outboxService, liveMatchSimulationManager);
    }
}
