package com.poorbet.matchservice.match.match.mapper;

import com.poorbet.matchservice.match.match.dto.request.PreMatchDto;
import com.poorbet.matchservice.match.match.dto.request.PredictionBatchRequestDto;
import com.poorbet.matchservice.match.match.dto.request.PredictionMatchDto;

import java.util.List;

public class PredictionBatchMapper {

    public static PredictionBatchRequestDto toPredictionBatchRequestDto(List<PreMatchDto> preMatches) {
        List<PredictionMatchDto> matches = preMatches.stream()
                .map(preMatchDto -> new PredictionMatchDto(
                        preMatchDto.getMatchId(),
                        preMatchDto.getHomeAttack(),
                        preMatchDto.getHomeDefense(),
                        preMatchDto.getAwayAttack(),
                        preMatchDto.getAwayDefense()
                ))
                .toList();

        return new PredictionBatchRequestDto(matches);
    }
}
