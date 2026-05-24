package com.poorbet.odds_engine_service.bootstrap;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OddsEngineBootstrap implements ApplicationRunner {

    private final BootstrapService bootstrapService;

    @Override
    public void run(ApplicationArguments args) {
        bootstrapService.initIfNeeded();
    }
}
