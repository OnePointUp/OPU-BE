package com.opu.opube.feature.notification.query.service;

import com.opu.opube.feature.member.query.service.MemberQueryService;
import com.opu.opube.feature.notification.command.domain.repository.WebPushSubscriptionRepository;
import com.opu.opube.feature.notification.query.dto.WebPushStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WebPushStatusQueryService {

    private final MemberQueryService memberQueryService;
    private final WebPushSubscriptionRepository subscriptionRepository;

    @Transactional(readOnly = true)
    public WebPushStatusResponse getStatus(Long memberId) {
        boolean agreed = memberQueryService.getWebPushAgreed(memberId);
        boolean hasSubscription = subscriptionRepository.existsByMemberId(memberId);

        return new WebPushStatusResponse(agreed, hasSubscription);
    }
}