package com.opu.opube.feature.notification.command.domain.repository;

import com.opu.opube.feature.notification.command.domain.aggregate.WebPushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WebPushSubscriptionRepository extends JpaRepository<WebPushSubscription, Long> {

    Optional<WebPushSubscription> findByMemberIdAndEndpoint(Long memberId, String endpoint);

    List<WebPushSubscription> findAllByMemberId(Long memberId);

    void deleteByMemberIdAndEndpoint(Long memberId, String endpoint);

    boolean existsByMemberId(Long memberId);

    void deleteByMemberId(Long memberId);
}