package com.opu.opube.feature.notification.command.domain.repository;

import com.opu.opube.feature.notification.command.domain.aggregate.NotificationType;
import java.util.Optional;

public interface NotificationTypeRepository {
    Optional<NotificationType> findByCode(String code);
}