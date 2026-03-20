package com.poorbet.matchservice.match.match.controller;

import com.poorbet.matchservice.match.match.domain.OddsType;
import com.poorbet.matchservice.match.match.dto.MatchResultMapDto;
import com.poorbet.matchservice.match.match.service.MatchResultsService;
import com.poorbet.matchservice.match.match.service.OddsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/match")
public class InternalMatchController {

    private final MatchResultsService matchResultsService;
    private final OddsService oddsService;

    @PostMapping("/results")
    public ResponseEntity<MatchResultMapDto> getResults(@RequestBody List<UUID> matchIds) {
        MatchResultMapDto result = matchResultsService.getMatchResultMap(matchIds);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{matchId}/odds")
    public ResponseEntity<BigDecimal> getOdds(@PathVariable UUID matchId, @RequestParam OddsType type) {
        return oddsService.getOdds(matchId, type)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
