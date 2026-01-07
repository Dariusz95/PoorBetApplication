package com.poorbet.oddsservice.controller;

import com.poorbet.oddsservice.dto.OddsResponse;
import com.poorbet.oddsservice.dto.PredictOddsRequest;
import com.poorbet.oddsservice.service.OddsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/predict")
@RequiredArgsConstructor
public class OddsController {

    private final OddsService oddsService;

    @PostMapping
    public ResponseEntity<OddsResponse> predictOdds(@Valid @RequestBody PredictOddsRequest request) {
        log.debug("Predicting odds for match - Home Attack: {}, Home Defense: {}, Away Attack: {}, Away Defense: {}",
            request.homeTeamAttack(), request.homeTeamDefense(),
            request.awayTeamAttack(), request.awayTeamDefense());

        OddsResponse response = oddsService.predictOdds(
            request.homeTeamAttack(),
            request.homeTeamDefense(),
            request.awayTeamAttack(),
            request.awayTeamDefense()
        );

        return ResponseEntity.ok(response);
    }
}
