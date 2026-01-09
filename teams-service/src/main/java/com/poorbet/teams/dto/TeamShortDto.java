package com.poorbet.teams.dto;

import java.io.Serializable;
import java.util.UUID;

public record TeamShortDto(
        UUID id,
        String name
) implements Serializable {
}
