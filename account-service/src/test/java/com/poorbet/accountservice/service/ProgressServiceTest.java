package com.poorbet.accountservice.service;

import com.poorbet.accountservice.domain.model.AccountProgress;
import com.poorbet.accountservice.domain.model.LevelConfig;
import com.poorbet.accountservice.dto.AccountProgressResponse;
import com.poorbet.accountservice.repository.AccountProgressRepository;
import com.poorbet.commons.commons.account.AccountBatchLookupResponse;
import com.poorbet.commons.commons.account.AccountLevelDto;
import com.poorbet.commons.rabbit.events.account.AccountProgressChangedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProgressService Unit Tests")
class ProgressServiceTest {

    @Mock
    private AccountProgressRepository accountProgressRepository;
    @Mock
    private LevelConfigService levelConfigService;
    @Mock
    private OutboxService outboxService;

    @InjectMocks
    private ProgressService progressService;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    private LevelConfig config(int level, int winBonusPercent) {
        return new LevelConfig(level, 0L, winBonusPercent, 3);
    }

    // ==================== addExpForStake ====================

    @Test
    @DisplayName("Should create progress at level 1 and accrue EXP for a first-time user")
    void shouldCreateProgressAndAccrueExpForNewUser() {
        // Arrange
        when(accountProgressRepository.findByUserIdForUpdate(userId)).thenReturn(Optional.empty());
        when(levelConfigService.findByCurrentExp(50L)).thenReturn(config(1, 1));
        when(levelConfigService.findNextLevel(1)).thenReturn(Optional.of(config(2, 2)));

        // Act
        progressService.addExpForStake(userId, new BigDecimal("50.00"));

        // Assert
        ArgumentCaptor<AccountProgress> captor = ArgumentCaptor.forClass(AccountProgress.class);
        verify(accountProgressRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(userId);
        assertThat(captor.getValue().getCurrentExp()).isEqualTo(50L);
        assertThat(captor.getValue().getLevel()).isEqualTo(1);

        ArgumentCaptor<AccountProgressChangedEvent> eventCaptor =
                ArgumentCaptor.forClass(AccountProgressChangedEvent.class);
        verify(outboxService).saveEvent(any(), eventCaptor.capture());
        assertThat(eventCaptor.getValue().userId()).isEqualTo(userId);
        assertThat(eventCaptor.getValue().level()).isEqualTo(1);
        assertThat(eventCaptor.getValue().currentExp()).isEqualTo(50L);
        assertThat(eventCaptor.getValue().requiredExpForNextLevel()).isEqualTo(0L);
        assertThat(eventCaptor.getValue().winBonusPercent()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should accumulate EXP on top of existing progress and jump several levels at once")
    void shouldAccumulateExpAndJumpMultipleLevels() {
        // Arrange
        AccountProgress existing = AccountProgress.createForUser(userId);
        existing.setCurrentExp(80L);
        existing.setLevel(1);
        when(accountProgressRepository.findByUserIdForUpdate(userId)).thenReturn(Optional.of(existing));
        when(levelConfigService.findByCurrentExp(980L)).thenReturn(config(5, 5));
        when(levelConfigService.findNextLevel(5)).thenReturn(Optional.of(config(6, 6)));

        // Act
        progressService.addExpForStake(userId, new BigDecimal("900.00"));

        // Assert
        ArgumentCaptor<AccountProgress> captor = ArgumentCaptor.forClass(AccountProgress.class);
        verify(accountProgressRepository).save(captor.capture());
        assertThat(captor.getValue().getCurrentExp()).isEqualTo(980L);
        assertThat(captor.getValue().getLevel()).isEqualTo(5);

        ArgumentCaptor<AccountProgressChangedEvent> eventCaptor =
                ArgumentCaptor.forClass(AccountProgressChangedEvent.class);
        verify(outboxService).saveEvent(any(), eventCaptor.capture());
        assertThat(eventCaptor.getValue().userId()).isEqualTo(userId);
        assertThat(eventCaptor.getValue().level()).isEqualTo(5);
        assertThat(eventCaptor.getValue().currentExp()).isEqualTo(980L);
        assertThat(eventCaptor.getValue().winBonusPercent()).isEqualTo(5);
    }

    // ==================== getProgressView ====================

    @Test
    @DisplayName("Should build a progress view with the required EXP for the next level")
    void shouldBuildProgressViewWithNextLevelRequirement() {
        // Arrange
        AccountProgress progress = AccountProgress.createForUser(userId);
        progress.setCurrentExp(120L);
        progress.setLevel(2);
        when(accountProgressRepository.findById(userId)).thenReturn(Optional.of(progress));
        when(levelConfigService.findByLevel(2)).thenReturn(config(2, 2));
        when(levelConfigService.findNextLevel(2)).thenReturn(Optional.of(config(3, 3)));

        // Act
        AccountProgressResponse response = progressService.getProgressView(userId);

        // Assert
        assertThat(response.level()).isEqualTo(2);
        assertThat(response.currentExp()).isEqualTo(120L);
        assertThat(response.winBonusPercent()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should return a default level-1 view for a user without any progress yet")
    void shouldReturnDefaultProgressViewForNewUser() {
        // Arrange
        when(accountProgressRepository.findById(userId)).thenReturn(Optional.empty());
        when(levelConfigService.findByLevel(1)).thenReturn(config(1, 1));
        when(levelConfigService.findNextLevel(1)).thenReturn(Optional.of(config(2, 2)));

        // Act
        AccountProgressResponse response = progressService.getProgressView(userId);

        // Assert
        assertThat(response.level()).isEqualTo(1);
        assertThat(response.currentExp()).isEqualTo(0L);
    }

    // ==================== getLevelsBatch ====================

    @Test
    @DisplayName("Should default missing users to level 1 in the batch lookup")
    void shouldDefaultMissingUsersToLevelOneInBatchLookup() {
        // Arrange
        UUID knownUserId = UUID.randomUUID();
        UUID unknownUserId = UUID.randomUUID();
        AccountProgress knownProgress = AccountProgress.createForUser(knownUserId);
        knownProgress.setLevel(7);

        when(accountProgressRepository.findAllById(anyCollection())).thenReturn(List.of(knownProgress));

        // Act
        AccountBatchLookupResponse response = progressService.getLevelsBatch(Set.of(knownUserId, unknownUserId));

        // Assert
        assertThat(response.accounts()).containsEntry(knownUserId, new AccountLevelDto(knownUserId, 7));
        assertThat(response.accounts()).containsEntry(unknownUserId, new AccountLevelDto(unknownUserId, 1));
    }
}
