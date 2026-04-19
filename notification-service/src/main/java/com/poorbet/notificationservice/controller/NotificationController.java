package com.poorbet.notificationservice.controller;

import com.poorbet.notificationservice.security.CurrentUserProvider;
import com.poorbet.notificationservice.service.SseNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final SseNotificationService sseNotificationService;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        return sseNotificationService.register(currentUserProvider.getUserId());
    }

}
