package com.opu.opube.feature.notification.command.infrastructure.repository;

import com.opu.opube.feature.notification.command.domain.aggregate.WebPushSubscription;
import com.opu.opube.feature.notification.command.domain.repository.WebPushSubscriptionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface JpaWebPushSubscriptionRepository
        extends JpaRepository<WebPushSubscription, Long>, WebPushSubscriptionRepository {

    @Override
    @Modifying
    @Query("delete from WebPushSubscription w where w.member.id = :memberId")
    void deleteByMemberId(Long memberId);
}