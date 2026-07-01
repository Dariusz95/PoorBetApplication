package com.poorbet.matchservice.match.match.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poorbet.matchservice.match.match.domain.OddsType;
import com.poorbet.matchservice.match.match.dto.BetSnapshotRequest;
import com.poorbet.matchservice.match.match.dto.MatchBetSnapshotDto;
import com.poorbet.matchservice.match.match.dto.MatchResultMapDto;
import com.poorbet.matchservice.match.match.service.BetSnapshotService;
import com.poorbet.matchservice.match.match.service.MatchResultsService;
import com.poorbet.matchservice.match.match.service.OddsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/match")
public class InternalMatchController {

    private final MatchResultsService matchResultsService;
    private final OddsService oddsService;
    private final BetSnapshotService betSnapshotService;

    @PostMapping("/results")
    public ResponseEntity<MatchResultMapDto> getResults(@RequestBody @NotEmpty List<UUID> matchIds) {
        MatchResultMapDto result = matchResultsService.getMatchResultMap(matchIds);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{matchId}/odds")
    public ResponseEntity<BigDecimal> getOdds(@PathVariable UUID matchId, @RequestParam OddsType type) {
        return oddsService.getOdds(matchId, type)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{matchId}/bet-snapshot")
    public ResponseEntity<MatchBetSnapshotDto> getBetSnapshot(@PathVariable UUID matchId, @RequestParam OddsType type) {
        try {
            MatchBetSnapshotDto snapshot = betSnapshotService.getBetSnapshot(matchId, type);
            return ResponseEntity.ok(snapshot);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/bet-snapshots/batch")
    public ResponseEntity<List<MatchBetSnapshotDto>> getBetSnapshotsBatch(@RequestBody @Valid @NotEmpty List<BetSnapshotRequest> requests) {
        return ResponseEntity.ok(betSnapshotService.getBetSnapshots(requests));
    }
}
