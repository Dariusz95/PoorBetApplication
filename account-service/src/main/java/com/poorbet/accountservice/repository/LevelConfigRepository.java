package com.poorbet.accountservice.repository;

import com.poorbet.accountservice.domain.model.LevelConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LevelConfigRepository extends JpaRepository<LevelConfig, Integer> {

    Optional<LevelConfig> findFirstByRequiredExperienceLessThanEqualOrderByLevelDesc(long currentExp);
}
