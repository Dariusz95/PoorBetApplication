package com.poorbet.matchservice.match.match.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PredictionBatchRequestDto {
    @NotNull
    private List<PredictionMatchDto> matches;
}
