package com.opu.opube.feature.notification.command.application.service;

import com.opu.opube.feature.notification.command.domain.aggregate.NotificationTypeCode;

public interface NotificationCommandService {

    void sendNotification(
            Long memberId,
            NotificationTypeCode typeCode,
            String title,
            String message,
            Long linkedContentId
    );

    void markAsRead(Long memberId, Long notificationId);

    void markAllAsRead(Long memberId);

    void updateSetting(Long memberId, String code, boolean enabled);

}