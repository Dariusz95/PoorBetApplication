package com.poorbet.matchservice.match.stream.dto;

import com.poorbet.matchservice.match.stream.model.enums.MatchStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchResultDto {
    UUID id;
    int homeGoals;
    int awayGoals;
    MatchStatus status;
}
