package com.poorbet.matchservice.match.stream.controller;

import com.poorbet.matchservice.match.stream.dto.LiveMatchEventDto;
import com.poorbet.matchservice.match.stream.dto.response.MatchPoolDto;
import com.poorbet.matchservice.match.stream.service.LiveMatchSimulationManager;
import com.poorbet.matchservice.match.stream.service.MatchService;
import com.poorbet.matchservice.match.stream.service.MatchServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/match")
public class MatchController {

    private final LiveMatchSimulationManager manager;
    private final MatchService matchService;

    @GetMapping(
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
        return matchService.getFutureMatchPools();
    }
}