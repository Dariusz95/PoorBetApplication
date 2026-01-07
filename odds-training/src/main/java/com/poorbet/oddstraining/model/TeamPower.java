package com.poorbet.oddstraining.model;

import org.hibernate.validator.constraints.UUID;

public record TeamPower(
        int attackPower,
        int defencePower
) {}

