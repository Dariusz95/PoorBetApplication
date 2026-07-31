package com.poorbet.accountservice.service;

import com.poorbet.accountservice.domain.model.LevelConfig;
import com.poorbet.accountservice.repository.LevelConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LevelConfigService {

    private final LevelConfigRepository levelConfigRepository;

    public LevelConfig findByCurrentExp(long currentExp) {
        return levelConfigRepository
                .findFirstByRequiredExperienceLessThanEqualOrderByLevelDesc(currentExp)
                .orElseThrow(() -> new IllegalStateException("Brak konfiguracji poziomu dla currentExp=" + currentExp));
    }

    public LevelConfig findByLevel(int level) {
        return levelConfigRepository.findById(level)
                .orElseThrow(() -> new IllegalStateException("Brak konfiguracji dla level=" + level));
    }

    public Optional<LevelConfig> findNextLevel(int currentLevel) {
        return levelConfigRepository.findById(currentLevel + 1);
    }
}
