package com.poorbet.matchservice.match.match.dto.request;

import java.util.List;
import java.util.UUID;

public record TeamStatsRequest (
    List<UUID> teamIds
){}
