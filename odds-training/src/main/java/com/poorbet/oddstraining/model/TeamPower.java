package com.poorbet.oddstraining.model;

import org.hibernate.validator.constraints.UUID;

public record TeamPower(
        double attackPower,
        double defencePower
) {}

