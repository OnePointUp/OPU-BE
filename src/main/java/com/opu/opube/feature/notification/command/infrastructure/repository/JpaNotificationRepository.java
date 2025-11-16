package com.opu.opube.feature.notification.command.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.opu.opube.feature.notification.command.domain.aggregate.Notification;
import com.opu.opube.feature.notification.command.domain.repository.NotificationRepository;

public interface JpaNotificationRepository extends NotificationRepository, JpaRepository<Notification, Long> {
}