package com.opu.opube.feature.notification.command.application.service;

import com.opu.opube.feature.notification.command.application.dto.response.NotificationResponse;
import com.opu.opube.feature.notification.command.infrastructure.repository.NotificationSseEmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationSseService {

    private static final long DEFAULT_TIMEOUT = 60L * 60L * 1000L; // 1시간

    private final NotificationSseEmitterRepository emitterRepository;

    public SseEmitter connect(Long memberId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        emitterRepository.add(memberId, emitter);

        emitter.onCompletion(() -> emitterRepository.remove(memberId, emitter));
        emitter.onTimeout(() -> emitterRepository.remove(memberId, emitter));
        emitter.onError(e -> emitterRepository.remove(memberId, emitter));

        // 더미 이벤트 한 번 보내서 연결 유지
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected"));
        } catch (IOException e) {
            emitterRepository.remove(memberId, emitter);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    public void sendToMember(Long memberId, NotificationResponse dto) {
        List<SseEmitter> emitters = emitterRepository.get(memberId);
        if (emitters.isEmpty()) return;

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(dto));
            } catch (IOException e) {
                emitterRepository.remove(memberId, emitter);
                emitter.completeWithError(e);
            }
        }
    }
}