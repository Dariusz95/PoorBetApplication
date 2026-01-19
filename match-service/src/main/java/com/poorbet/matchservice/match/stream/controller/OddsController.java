package com.poorbet.matchservice.match.stream.controller;

import com.poorbet.matchservice.match.stream.model.enums.OddsType;
import com.poorbet.matchservice.match.stream.service.OddsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/match")
public class OddsController {

    private final OddsService oddsService;

    @GetMapping("/{matchId}/odds")
    public ResponseEntity<BigDecimal> getOdds(@PathVariable UUID matchId, @RequestParam OddsType type) {
        return oddsService.getOdds(matchId, type)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
