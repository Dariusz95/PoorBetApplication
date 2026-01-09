package com.poorbet.matchservice.match.stream;

import com.poorbet.matchservice.match.stream.dto.LiveMatchEventDto;
import com.poorbet.matchservice.match.stream.repository.MatchRepository;
import com.poorbet.matchservice.match.stream.service.LiveMatchSimulationManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/match")
public class MatchStreamController {

    private final LiveMatchSimulationManager manager;

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
}