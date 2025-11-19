package com.opu.opube.feature.notification.command.domain.repository;

import com.opu.opube.feature.notification.command.domain.aggregate.Notification;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Optional<Notification> findByIdAndMember_Id(Long id, Long memberId);

    @Modifying(clearAutomatically = true)
    @Query("update Notification n set n.isRead = true " +
            "where n.member.id = :memberId and n.isRead = false")
    void markAllAsReadByMemberId(@Param("memberId") Long memberId);
}