package com.poorbet.odds_engine_service.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class SystemState {

    private final AtomicBoolean ready = new AtomicBoolean(false);

    public void markReady() {
        log.info("ready");
        ready.set(true);
    }

    public boolean isReady() {
        return ready.get();
    }
}
