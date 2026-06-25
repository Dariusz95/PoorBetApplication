package com.poorbet.matchservice.team.dto;

import java.io.Serializable;
import java.util.UUID;

public record TeamShortDto(
        UUID id,
        String name,
        String img
) implements Serializable {
}
