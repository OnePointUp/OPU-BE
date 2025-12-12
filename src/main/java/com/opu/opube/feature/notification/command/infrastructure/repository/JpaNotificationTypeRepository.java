package com.opu.opube.feature.notification.command.infrastructure.repository;

import com.opu.opube.feature.notification.command.domain.aggregate.NotificationType;
import com.opu.opube.feature.notification.command.domain.repository.NotificationTypeRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaNotificationTypeRepository extends NotificationTypeRepository, JpaRepository<NotificationType, Long> {
}
