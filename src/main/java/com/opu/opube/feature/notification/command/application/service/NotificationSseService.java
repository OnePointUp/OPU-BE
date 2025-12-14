package com.opu.opube.feature.notification.command.application.service;

import com.opu.opube.feature.notification.command.application.dto.response.NotificationResponse;
import com.opu.opube.feature.notification.command.infrastructure.repository.NotificationSseEmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationSseService {

    private static final long DEFAULT_TIMEOUT_MS = 2L * 60L * 60L * 1000L; // 2시간

    private final NotificationSseEmitterRepository emitterRepository;

    public SseEmitter connect(Long memberId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT_MS);

        emitterRepository.add(memberId, emitter);

        emitter.onCompletion(() -> emitterRepository.remove(memberId, emitter));
        emitter.onTimeout(() -> emitterRepository.remove(memberId, emitter));
        emitter.onError(ex -> emitterRepository.remove(memberId, emitter));

        safeSend(memberId, emitter,
                SseEmitter.event()
                        .name("connect")
                        .data("connected")
        );

        return emitter;
    }

    public void sendToMember(Long memberId, NotificationResponse dto) {
        List<SseEmitter> emitters = emitterRepository.get(memberId);
        if (emitters.isEmpty()) return;

        for (SseEmitter emitter : List.copyOf(emitters)) {
            SseEmitter.SseEventBuilder event = SseEmitter.event()
                    .name("notification")
                    .data(dto);

            if (dto.getId() != null) {
                event = event.id(String.valueOf(dto.getId()));
            }

            safeSend(memberId, emitter, event);
        }
    }

    public void sendPing(Long memberId) {
        List<SseEmitter> emitters = emitterRepository.get(memberId);
        if (emitters.isEmpty()) return;

        for (SseEmitter emitter : List.copyOf(emitters)) {
            safeSend(memberId, emitter,
                    SseEmitter.event()
                            .name("ping")
                            .data("ok")
            );
        }
    }

    private void safeSend(Long memberId, SseEmitter emitter, SseEmitter.SseEventBuilder event) {
        try {
            emitter.send(event);
        } catch (Exception ex) {
            emitterRepository.remove(memberId, emitter);
            try {
                emitter.complete();
            } catch (Exception ignored) {
            }
            log.debug("SSE send failed. memberId={}", memberId, ex);
        }
    }
}