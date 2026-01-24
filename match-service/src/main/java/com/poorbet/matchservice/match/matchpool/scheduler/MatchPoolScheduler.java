package com.poorbet.matchservice.match.matchpool.scheduler;

import com.poorbet.matchservice.match.matchpool.service.MatchPoolSchedulingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchPoolScheduler {

    private final MatchPoolSchedulingService schedulingService;

    @Value("${INSTANCE_ID:local}")
    private String instanceId;

    @Scheduled(fixedRateString = "#{@matchPoolProperties.scheduleRateMs}")
    @SchedulerLock(name = "scheduleMissingPools", lockAtMostFor = "PT2M", lockAtLeastFor = "PT1M")
    public void checkAndFillPools() {
        schedulingService.scheduleMissingPools();
        log.info("✅ Instance {}: invoked scheduleMissingPools", instanceId);
    }
}
