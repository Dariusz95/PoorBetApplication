package com.poorbet.odds_engine_service.health;

import com.poorbet.odds_engine_service.lifecycle.SystemState;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModelReadinessHealthIndicator implements HealthIndicator {

    private final SystemState systemState;

    @Override
    public Health health() {

        if (systemState.isReady()) {
            return Health.up()
                    .withDetail("ml", "READY")
                    .build();
        }

        return Health.down()
                .withDetail("ml", systemState.getStatus())
                .build();
    }
}