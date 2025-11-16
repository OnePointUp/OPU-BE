package com.opu.opube.feature.notification.query.infrastructure.repository;

import java.util.List;
import com.opu.opube.feature.notification.command.domain.aggregate.Notification;

public interface NotificationQueryRepository {
    List<Notification> findAll();
}