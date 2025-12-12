package com.opu.opube.feature.notification.command.infrastructure.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class NotificationSseEmitterRepository {

    private final Map<Long, CopyOnWriteArrayList<SseEmitter>> store = new ConcurrentHashMap<>();

    public void add(Long memberId, SseEmitter emitter) {
        store.computeIfAbsent(memberId, k -> new CopyOnWriteArrayList<>()).add(emitter);
    }

    public void remove(Long memberId, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> list = store.get(memberId);
        if (list == null) return;

        list.remove(emitter);
        if (list.isEmpty()) {
            store.remove(memberId);
        }
    }

    public List<SseEmitter> get(Long memberId) {
        return store.getOrDefault(memberId, new CopyOnWriteArrayList<>());
    }

    public Map<Long, CopyOnWriteArrayList<SseEmitter>> snapshotAll() {
        return store;
    }
}