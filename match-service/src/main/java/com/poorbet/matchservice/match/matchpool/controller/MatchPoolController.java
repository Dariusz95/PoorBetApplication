package com.poorbet.matchservice.match.matchpool.controller;

import com.poorbet.matchservice.match.matchpool.dto.LiveMatchEventDto;
import com.poorbet.matchservice.match.matchpool.dto.MatchPoolDto;
import com.poorbet.matchservice.match.matchpool.service.LiveMatchSimulationManager;
import com.poorbet.matchservice.match.matchpool.service.MatchPoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/match-pool")
public class MatchPoolController {

    private final LiveMatchSimulationManager manager;
    private final MatchPoolService matchPoolService;

    @GetMapping(
            path = "/live",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public Flux<LiveMatchEventDto> streamAll() {
        return Flux.merge(
                Flux.just(LiveMatchEventDto.heartbeat()),
                manager.streamAll()
        ).doOnSubscribe(sub ->
                log.info("New client subscribed to live match stream")
        ).doOnCancel(() ->
                log.info("Client unsubscribed from live match stream")
        );
    }

    @GetMapping("/future")
    public List<MatchPoolDto> getFutureMatchPools(){
        return matchPoolService.getFutureMatchPools();
    }
}