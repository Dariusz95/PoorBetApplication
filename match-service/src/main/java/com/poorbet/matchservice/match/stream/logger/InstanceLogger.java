package com.poorbet.matchservice.match.stream.logger;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InstanceLogger {
    @Value("${INSTANCE_ID:local}")
    private String instanceId;

    @PostConstruct
    void init() {
        log.info("🚀 Match service started on instance {}", instanceId);
    }
}
