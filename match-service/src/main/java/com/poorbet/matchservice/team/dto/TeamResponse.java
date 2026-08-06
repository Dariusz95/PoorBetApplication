package com.poorbet.matchservice.team.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TeamResponse {
    private UUID id;
    private String name;
    private int attackPower;
    private int defencePower;
}