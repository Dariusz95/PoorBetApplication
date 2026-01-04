package com.poorbet.matchservice.match.stream.request;

import java.util.List;
import java.util.UUID;

public record TeamStatsRequest (
    List<UUID> teamIds
){}
