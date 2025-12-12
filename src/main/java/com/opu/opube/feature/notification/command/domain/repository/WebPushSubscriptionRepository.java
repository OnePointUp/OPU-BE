package com.opu.opube.feature.notification.command.domain.repository;

import com.opu.opube.feature.notification.command.domain.aggregate.WebPushSubscription;

import java.util.List;
import java.util.Optional;

public interface WebPushSubscriptionRepository {

    Optional<WebPushSubscription> findByMemberIdAndEndpoint(Long memberId, String endpoint);

    List<WebPushSubscription> findAllByMemberId(Long memberId);

    void deleteByMemberIdAndEndpoint(Long memberId, String endpoint);

    boolean existsByMemberId(Long memberId);

    void deleteByMemberId(Long memberId);

    WebPushSubscription save(WebPushSubscription subscription);

    void delete(WebPushSubscription sub);
}