package com.poorbet.notificationservice.controller;

import com.poorbet.notificationservice.service.WalletNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final WalletNotificationService walletNotificationService;

    @GetMapping("/wallet/live")
    public SseEmitter streamWallet(@RequestParam String subject) {
        UUID userId = UUID.nameUUIDFromBytes(subject.getBytes(StandardCharsets.UTF_8));
        return walletNotificationService.register(userId);
    }
}
