package com.poorbet.accountservice.service;

import com.poorbet.accountservice.domain.model.AccountProgress;
import com.poorbet.accountservice.domain.model.LevelConfig;
import com.poorbet.accountservice.dto.AccountProgressResponse;
import com.poorbet.accountservice.repository.AccountProgressRepository;
import com.poorbet.commons.commons.account.AccountBatchLookupResponse;
import com.poorbet.commons.commons.account.AccountLevelDto;
import com.poorbet.commons.rabbit.events.account.AccountEvents;
import com.poorbet.commons.rabbit.events.account.AccountProgressChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private static final int DEFAULT_LEVEL = 1;

    private final AccountProgressRepository accountProgressRepository;
    private final LevelConfigService levelConfigService;
    private final OutboxService outboxService;

    @Transactional
    public void addExpForStake(UUID userId, BigDecimal stake) {
        AccountProgress progress = accountProgressRepository.findByUserIdForUpdate(userId)
                .orElseGet(() -> AccountProgress.createForUser(userId));

        long newExp = progress.getCurrentExp() + stake.longValue();
        progress.setCurrentExp(newExp);

        LevelConfig levelConfig = levelConfigService.findByCurrentExp(newExp);
        progress.setLevel(levelConfig.getLevel());

        accountProgressRepository.save(progress);

        Long requiredExpForNextLevel = levelConfigService.findNextLevel(levelConfig.getLevel())
                .map(LevelConfig::getRequiredExperience)
                .orElse(null);

        outboxService.saveEvent(
                AccountEvents.ACCOUNT_PROGRESS_CHANGED,
                new AccountProgressChangedEvent(
                        userId,
                        levelConfig.getLevel(),
                        newExp,
                        requiredExpForNextLevel,
                        levelConfig.getWinBonusPercent()
                )
        );
    }

    @Transactional(readOnly = true)
    public AccountProgressResponse getProgressView(UUID userId) {
        AccountProgress progress = accountProgressRepository.findById(userId)
                .orElseGet(() -> AccountProgress.createForUser(userId));

        LevelConfig currentLevelConfig = levelConfigService.findByLevel(progress.getLevel());
        Long requiredExpForNextLevel = levelConfigService.findNextLevel(progress.getLevel())
                .map(LevelConfig::getRequiredExperience)
                .orElse(null);

        return new AccountProgressResponse(
                progress.getLevel(),
                progress.getCurrentExp(),
                requiredExpForNextLevel,
                currentLevelConfig.getWinBonusPercent()
        );
    }

    @Transactional(readOnly = true)
    public AccountBatchLookupResponse getLevelsBatch(Set<UUID> userIds) {
        Map<UUID, Integer> levelsByUserId = accountProgressRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(AccountProgress::getUserId, AccountProgress::getLevel));

        Map<UUID, AccountLevelDto> accounts = userIds.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        userId -> new AccountLevelDto(userId, levelsByUserId.getOrDefault(userId, DEFAULT_LEVEL))
                ));

        return new AccountBatchLookupResponse(accounts);
    }
}
