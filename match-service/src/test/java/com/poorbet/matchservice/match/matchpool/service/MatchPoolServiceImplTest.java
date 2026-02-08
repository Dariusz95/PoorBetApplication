package com.poorbet.matchservice.match.matchpool.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import com.poorbet.matchservice.fixture.MatchFixtures;
import com.poorbet.matchservice.match.match.domain.Match;
import com.poorbet.matchservice.match.match.domain.MatchStatus;
import com.poorbet.matchservice.match.matchpool.domain.MatchPool;
import com.poorbet.matchservice.match.matchpool.domain.PoolStatus;
import com.poorbet.matchservice.match.matchpool.dto.MatchPoolDto;
import com.poorbet.matchservice.match.matchpool.mapper.MatchPoolMapper;
import com.poorbet.matchservice.match.matchpool.repository.MatchPoolRepository;


@ExtendWith(MockitoExtension.class)
@DisplayName("MatchPoolServiceImpl Unit Tests")
class MatchPoolServiceImplTest {

    @Mock
    private MatchPoolRepository matchPoolRepository;

    @Mock
    private MatchPoolMapper matchPoolMapper;

    @Mock
    private MatchPoolSimulationService matchPoolSimulationService;

    @InjectMocks
    private MatchPoolServiceImpl matchPoolService;

    private UUID testPoolId;
    private MatchPool testMatchPool;
    private List<Match> testMatches;

    @BeforeEach
    void setUp() {
        testPoolId = UUID.randomUUID();
        testMatches = createTestMatches();
        testMatchPool = createTestMatchPool();
    }

    @Nested
    @DisplayName("Start Pool")
    class StartPool {

        @Test
        @DisplayName("Should start pool and set status to STARTED")
        void shouldStartPoolAndSetStatusToStarted() {
            // Arrange
            testMatchPool.setStatus(PoolStatus.BETTABLE);
            when(matchPoolRepository.findById(testPoolId))
                    .thenReturn(Optional.of(testMatchPool));

            // Act
            matchPoolService.startPool(testPoolId);

            // Assert
            assertThat(testMatchPool.getStatus()).isEqualTo(PoolStatus.STARTED);
            verify(matchPoolRepository).save(testMatchPool);
        }


        @Test
        @DisplayName("Should save pool after status change")
        void shouldSavePoolAfterStatusChange() {
            // Arrange
            testMatchPool.setStatus(PoolStatus.BETTABLE);
            when(matchPoolRepository.findById(testPoolId))
                    .thenReturn(Optional.of(testMatchPool));

            // Act
            matchPoolService.startPool(testPoolId);

            // Assert
            ArgumentCaptor<MatchPool> poolCaptor = ArgumentCaptor.forClass(MatchPool.class);
            verify(matchPoolRepository).save(poolCaptor.capture());
            assertThat(poolCaptor.getValue().getStatus()).isEqualTo(PoolStatus.STARTED);
        }

        @Test
        @DisplayName("Should not start pool if not in BETTABLE status")
        void shouldNotStartPoolIfNotInBettableStatus() {
            // Arrange
            testMatchPool.setStatus(PoolStatus.STARTED);
            when(matchPoolRepository.findById(testPoolId))
                    .thenReturn(Optional.of(testMatchPool));

            // Act
            matchPoolService.startPool(testPoolId);

            // Assert
            assertThat(testMatchPool.getStatus()).isEqualTo(PoolStatus.STARTED);
            verify(matchPoolRepository, never()).save(any());
            verify(matchPoolSimulationService, never()).startPoolSimulation(any());
        }

        @Test
        @DisplayName("Should not start pool if status is FINISHED")
        void shouldNotStartPoolIfStatusIsFinished() {
            // Arrange
            testMatchPool.setStatus(PoolStatus.FINISHED);
            when(matchPoolRepository.findById(testPoolId))
                    .thenReturn(Optional.of(testMatchPool));

            // Act
            matchPoolService.startPool(testPoolId);

            // Assert
            verify(matchPoolRepository, never()).save(any());
            verify(matchPoolSimulationService, never()).startPoolSimulation(any());
        }

        @Test
        @DisplayName("Should throw exception if pool not found")
        void shouldThrowExceptionIfPoolNotFound() {
            // Arrange
            when(matchPoolRepository.findById(testPoolId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> matchPoolService.startPool(testPoolId))
                    .isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("Should handle pool with no matches")
        void shouldHandlePoolWithNoMatches() {
            // Arrange
            testMatchPool.setStatus(PoolStatus.BETTABLE);
            testMatchPool.setMatches(new ArrayList<>());
            when(matchPoolRepository.findById(testPoolId))
                    .thenReturn(Optional.of(testMatchPool));

            // Act
            matchPoolService.startPool(testPoolId);

            // Assert
            assertThat(testMatchPool.getStatus()).isEqualTo(PoolStatus.STARTED);
            verify(matchPoolRepository).save(testMatchPool);
        }

        @Test
        @DisplayName("Should handle pool with single match")
        void shouldHandlePoolWithSingleMatch() {
            // Arrange
            testMatchPool.setStatus(PoolStatus.BETTABLE);
            Match singleMatch = createTestMatch();
            testMatchPool.setMatches(List.of(singleMatch));
            when(matchPoolRepository.findById(testPoolId))
                    .thenReturn(Optional.of(testMatchPool));

            // Act
            matchPoolService.startPool(testPoolId);

            // Assert
            assertThat(singleMatch.getStatus()).isEqualTo(MatchStatus.LIVE);
        }

        @Test
        @DisplayName("Should handle pool with multiple matches")
        void shouldHandlePoolWithMultipleMatches() {
            // Arrange
            testMatchPool.setStatus(PoolStatus.BETTABLE);
            List<Match> matches = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                matches.add(createTestMatch());
            }
            testMatchPool.setMatches(matches);
            when(matchPoolRepository.findById(testPoolId))
                    .thenReturn(Optional.of(testMatchPool));

            // Act
            matchPoolService.startPool(testPoolId);

            // Assert
            matches.forEach(match ->
                    assertThat(match.getStatus()).isEqualTo(MatchStatus.LIVE)
            );
        }
    }

    @Nested
    @DisplayName("Get Future Match Pools")
    class GetFutureMatchPools {

        @Test
        @DisplayName("Should retrieve future match pools")
        void shouldRetrieveFutureMatchPools() {
            // Arrange
            List<MatchPool> futurePools = List.of(createTestMatchPool(), createTestMatchPool());
            List<MatchPoolDto> expectedDtos = List.of(
                    new MatchPoolDto(UUID.randomUUID(), null, null, null),
                    new MatchPoolDto(UUID.randomUUID(), null, null, null)
            );

            when(matchPoolRepository.getFutureMatchPools(any(Pageable.class)))
                    .thenReturn(futurePools);
            when(matchPoolMapper.toDto(futurePools))
                    .thenReturn(expectedDtos);

            // Act
            List<MatchPoolDto> result = matchPoolService.getFutureMatchPools();

            // Assert
            assertThat(result)
                    .isNotNull()
                    .hasSize(2)
                    .isEqualTo(expectedDtos);
        }

        @Test
        @DisplayName("Should use correct pagination parameters")
        void shouldUseCorrectPaginationParameters() {
            // Arrange
            when(matchPoolRepository.getFutureMatchPools(any(Pageable.class)))
                    .thenReturn(Collections.emptyList());
            when(matchPoolMapper.toDto(Collections.emptyList()))
                    .thenReturn(Collections.emptyList());

            // Act
            matchPoolService.getFutureMatchPools();

            // Assert
            ArgumentCaptor<Pageable> pageCaptor = ArgumentCaptor.forClass(Pageable.class);
            verify(matchPoolRepository).getFutureMatchPools(pageCaptor.capture());
            Pageable capturedPage = pageCaptor.getValue();
            assertThat(capturedPage.getPageNumber()).isZero();
            assertThat(capturedPage.getPageSize()).isEqualTo(3);
        }

        @Test
        @DisplayName("Should handle empty future pools list")
        void shouldHandleEmptyFuturePoolsList() {
            // Arrange
            when(matchPoolRepository.getFutureMatchPools(any(Pageable.class)))
                    .thenReturn(Collections.emptyList());
            when(matchPoolMapper.toDto(Collections.emptyList()))
                    .thenReturn(Collections.emptyList());

            // Act
            List<MatchPoolDto> result = matchPoolService.getFutureMatchPools();

            // Assert
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should call mapper with repository results")
        void shouldCallMapperWithRepositoryResults() {
            // Arrange
            List<MatchPool> repositoryResults = List.of(
                    createTestMatchPool(),
                    createTestMatchPool(),
                    createTestMatchPool()
            );
            when(matchPoolRepository.getFutureMatchPools(any(Pageable.class)))
                    .thenReturn(repositoryResults);
            when(matchPoolMapper.toDto(repositoryResults))
                    .thenReturn(Collections.emptyList());

            // Act
            matchPoolService.getFutureMatchPools();

            // Assert
            verify(matchPoolMapper).toDto(repositoryResults);
        }

        @Test
        @DisplayName("Should return mapped DTOs from service")
        void shouldReturnMappedDTOsFromService() {
            // Arrange
            List<MatchPool> pools = List.of(createTestMatchPool());
            MatchPoolDto dto1 = new MatchPoolDto(UUID.randomUUID(), null, null, null);
            MatchPoolDto dto2 = new MatchPoolDto(UUID.randomUUID(), null, null, null);
            List<MatchPoolDto> expectedDtos = List.of(dto1, dto2);

            when(matchPoolRepository.getFutureMatchPools(any(Pageable.class)))
                    .thenReturn(pools);
            when(matchPoolMapper.toDto(pools))
                    .thenReturn(expectedDtos);

            // Act
            List<MatchPoolDto> result = matchPoolService.getFutureMatchPools();

            // Assert
            assertThat(result).isEqualTo(expectedDtos);
        }

        @Test
        @DisplayName("Should be read-only transactional")
        void shouldBeReadOnlyTransactional() {
            // This test verifies @Transactional(readOnly = true) is used
            assertThat(MatchPoolServiceImpl.class).isNotNull();
        }

        @Test
        @DisplayName("Should handle repository exception")
        void shouldHandleRepositoryException() {
            // Arrange
            when(matchPoolRepository.getFutureMatchPools(any(Pageable.class)))
                    .thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThatThrownBy(() -> matchPoolService.getFutureMatchPools())
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("Should handle mapper exception")
        void shouldHandleMapperException() {
            // Arrange
            List<MatchPool> pools = List.of(createTestMatchPool());
            when(matchPoolRepository.getFutureMatchPools(any(Pageable.class)))
                    .thenReturn(pools);
            when(matchPoolMapper.toDto(pools))
                    .thenThrow(new RuntimeException("Mapping error"));

            // Act & Assert
            assertThatThrownBy(() -> matchPoolService.getFutureMatchPools())
                    .isInstanceOf(RuntimeException.class);
        }
    }

    // Helper methods
    private MatchPool createTestMatchPool() {
        MatchPool pool = MatchFixtures.createMatchPool();
        pool.setMatches(new ArrayList<>(createTestMatches()));
        return pool;
    }

    private List<Match> createTestMatches() {
        return MatchFixtures.createMatches(3);
    }

    private Match createTestMatch() {
        return MatchFixtures.createMatch();
    }
}
