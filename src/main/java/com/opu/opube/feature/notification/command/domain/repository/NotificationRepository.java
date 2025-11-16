package com.opu.opube.feature.notification.command.domain.repository;

import java.util.UUID;
import com.opu.opube.feature.notification.command.domain.aggregate.Notification;

public interface NotificationRepository {
    Notification save(Notification notification);
}