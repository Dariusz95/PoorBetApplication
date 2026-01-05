package com.poorbet.matchservice.match.stream;

import com.poorbet.matchservice.match.stream.dto.LiveMatchEventDto;
import com.poorbet.matchservice.match.stream.service.LiveMatchSimulationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/match")
public class MatchStreamController {

    private final LiveMatchSimulationManager manager;

    @GetMapping(
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public Flux<LiveMatchEventDto> streamAll() {
        Flux<LiveMatchEventDto> heartbeat = Flux.interval(Duration.ofSeconds(5))
                .map(tick -> LiveMatchEventDto.heartbeat());

        return Flux.merge(manager.streamAll(), heartbeat);
    }
}