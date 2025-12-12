package com.opu.opube.feature.notification.command.application.service;

import com.opu.opube.feature.notification.command.infrastructure.repository.NotificationSseEmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationSseHeartbeatScheduler {

    private final NotificationSseService notificationSseService;
    private final NotificationSseEmitterRepository emitterRepository;

    @Scheduled(fixedDelay = 60_000)
    public void pingAll() {
        for (Long memberId : emitterRepository.snapshotAll().keySet()) {
            notificationSseService.sendPing(memberId);
        }
    }
}