package com.poorbet.couponservice.dto;

import com.poorbet.couponservice.dto.enums.MatchStatus;
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
