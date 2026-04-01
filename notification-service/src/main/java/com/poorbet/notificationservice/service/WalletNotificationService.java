package com.poorbet.notificationservice.service;

import com.poorbet.notificationservice.dto.WalletBalanceChangedEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class WalletNotificationService {

    private final ConcurrentHashMap<UUID, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter register(UUID userId) {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.computeIfAbsent(userId, ignored -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> remove(userId, emitter));
        emitter.onTimeout(() -> remove(userId, emitter));
        emitter.onError(ex -> remove(userId, emitter));

        return emitter;
    }

    public void publish(WalletBalanceChangedEvent event) {
        List<SseEmitter> userEmitters = emitters.getOrDefault(event.userId(), new CopyOnWriteArrayList<>());
        userEmitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event().name("wallet-balance-updated").data(event));
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        });
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
