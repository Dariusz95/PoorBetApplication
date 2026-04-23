package com.poorbet.notificationservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseNotificationService {

    private static final long EMITTER_TIMEOUT_MS = 30 * 60 * 1000L;
    private static final String CONNECTED_EVENT = "notification.connected";
    private static final String HEARTBEAT_EVENT = "notification.heartbeat";

    private final ConcurrentHashMap<UUID, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter register(UUID userId) {
        SseEmitter emitter = new SseEmitter(EMITTER_TIMEOUT_MS);
        emitters.computeIfAbsent(userId, ignored -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> remove(userId, emitter));
        emitter.onTimeout(() -> remove(userId, emitter));
        emitter.onError(ex -> remove(userId, emitter));

        send(userId, emitter, CONNECTED_EVENT, "connected");

        return emitter;
    }

    public void publish(UUID userId, String sseEventName, Object payload) {
        List<SseEmitter> userEmitters = emitters.getOrDefault(userId, new CopyOnWriteArrayList<>());
        userEmitters.forEach(emitter -> send(userId, emitter, sseEventName, payload));
    }

    @Scheduled(fixedDelay = 25_000)
    public void heartbeat() {
        emitters.forEach((userId, userEmitters) ->
                userEmitters.forEach(emitter -> send(userId, emitter, HEARTBEAT_EVENT, "ping"))
        );
    }

    private void send(UUID userId, SseEmitter emitter, String sseEventName, Object payload) {
        try {
            emitter.send(SseEmitter.event().name(sseEventName).data(payload));
        } catch (IOException e) {
            emitter.completeWithError(e);
            remove(userId, emitter);
        }
    }

    private void remove(UUID userId, SseEmitter emitter) {
        List<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters == null) {
            return;
        }

        userEmitters.remove(emitter);

        if (userEmitters.isEmpty()) {
            emitters.remove(userId);
        }
    }
}
