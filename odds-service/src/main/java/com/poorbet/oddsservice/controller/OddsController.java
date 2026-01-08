package com.poorbet.oddsservice.controller;

import com.poorbet.oddsservice.dto.response.BatchOddsResponse;
import com.poorbet.oddsservice.dto.OddsResponseDto;
import com.poorbet.oddsservice.dto.PredictOddsRequest;
import com.poorbet.oddsservice.dto.request.BatchPredictionRequest;
import com.poorbet.oddsservice.dto.response.BatchPredictionResponseDto;
import com.poorbet.oddsservice.service.OddsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/odds")
@RequiredArgsConstructor
public class OddsController {

    private final OddsService oddsService;

    @PostMapping("/predict")
    public ResponseEntity<OddsResponseDto> predictOdds(@Valid @RequestBody PredictOddsRequest request) {
        log.debug("Predicting odds for match - Home Attack: {}, Home Defense: {}, Away Attack: {}, Away Defense: {}",
            request.homeTeamAttack(), request.homeTeamDefense(),
            request.awayTeamAttack(), request.awayTeamDefense());

        OddsResponseDto response = oddsService.predictOdds(
            request.homeTeamAttack(),
            request.homeTeamDefense(),
            request.awayTeamAttack(),
            request.awayTeamDefense()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/predict/batch")
    public ResponseEntity<BatchPredictionResponseDto> predictBatchOdds(@RequestBody @Valid BatchPredictionRequest request) {

        List<BatchOddsResponse> oddsList = oddsService.predictBatch(request.matches());

        BatchPredictionResponseDto response = new BatchPredictionResponseDto(oddsList);

        return ResponseEntity.ok(response);
    }
}
