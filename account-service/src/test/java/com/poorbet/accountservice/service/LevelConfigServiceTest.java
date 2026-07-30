package com.poorbet.accountservice.service;

import com.poorbet.accountservice.domain.model.LevelConfig;
import com.poorbet.accountservice.repository.LevelConfigRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("LevelConfigService Unit Tests")
class LevelConfigServiceTest {

    @Mock
    private LevelConfigRepository levelConfigRepository;

    @InjectMocks
    private LevelConfigService levelConfigService;

    private LevelConfig config(int level, long requiredExperience, int winBonusPercent) {
        return new LevelConfig(level, requiredExperience, winBonusPercent, 3);
    }

    @Test
    @DisplayName("Should resolve the highest level whose threshold is met, even when several levels are skipped at once")
    void shouldResolveHighestLevelForCurrentExp() {
        // Arrange
        when(levelConfigRepository.findFirstByRequiredExperienceLessThanEqualOrderByLevelDesc(1000L))
                .thenReturn(Optional.of(config(6, 1500, 6)));

        // Act
        LevelConfig result = levelConfigService.findByCurrentExp(1000L);

        // Assert
        assertThat(result.getLevel()).isEqualTo(6);
    }

    @Test
    @DisplayName("Should throw when no level configuration matches the given experience")
    void shouldThrowWhenNoLevelConfigMatches() {
        // Arrange
        when(levelConfigRepository.findFirstByRequiredExperienceLessThanEqualOrderByLevelDesc(-1L))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> levelConfigService.findByCurrentExp(-1L))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Should return the requested level configuration by level number")
    void shouldFindByLevel() {
        // Arrange
        when(levelConfigRepository.findById(5)).thenReturn(Optional.of(config(5, 900, 5)));

        // Act
        LevelConfig result = levelConfigService.findByLevel(5);

        // Assert
        assertThat(result.getWinBonusPercent()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should return empty when there is no next level configured (ceiling reached)")
    void shouldReturnEmptyWhenNoNextLevel() {
        // Arrange
        when(levelConfigRepository.findById(16)).thenReturn(Optional.empty());

        // Act
        Optional<LevelConfig> result = levelConfigService.findNextLevel(15);

        // Assert
        assertThat(result).isEmpty();
    }
}
