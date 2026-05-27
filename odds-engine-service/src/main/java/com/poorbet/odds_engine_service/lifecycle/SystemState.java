package com.poorbet.odds_engine_service.lifecycle;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
public class SystemState {

    private final AtomicReference<SystemStatus> status =
            new AtomicReference<>(SystemStatus.STARTING);

    public boolean isReady() {
        return status.get() == SystemStatus.READY;
    }

    public void set(SystemStatus newStatus) {
        status.set(newStatus);
    }

    public void setError() {
        status.set(SystemStatus.ERROR);
    }

    public SystemStatus getStatus() {
        return status.get();
    }
}
