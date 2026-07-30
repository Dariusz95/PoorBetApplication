package com.poorbet.accountservice.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "level_config")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LevelConfig {

    @Id
    private Integer level;

    private long requiredExperience;

    private int winBonusPercent;

    private int dailyCouponLimit;
}
