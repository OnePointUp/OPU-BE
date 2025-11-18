package com.opu.opube.feature.notification.command.infrastructure.repository;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class NotificationSseEmitterRepository {

    private final Map<Long, List<SseEmitter>> store = new ConcurrentHashMap<>();

    public SseEmitter add(Long memberId, SseEmitter emitter) {
        store.computeIfAbsent(memberId, k -> new CopyOnWriteArrayList<>())
                .add(emitter);
        return emitter;
    }

    public List<SseEmitter> get(Long memberId) {
        return store.getOrDefault(memberId, List.of());
    }

    public void remove(Long memberId, SseEmitter emitter) {
        List<SseEmitter> emitters = store.get(memberId);
        if (emitters != null) {
            emitters.remove(emitter);
        }
    }
}