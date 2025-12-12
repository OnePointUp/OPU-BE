package com.opu.opube.feature.notification.query.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.member.query.service.MemberQueryService;
import com.opu.opube.feature.notification.command.domain.repository.WebPushSubscriptionRepository;
import com.opu.opube.feature.notification.query.dto.WebPushStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications/push")
public class WebPushStatusController {

    private final MemberQueryService memberQueryService;
    private final WebPushSubscriptionRepository subscriptionRepository;

    @GetMapping("/status")
    public ApiResponse<WebPushStatusResponse> getStatus(
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        Long memberId = principal.getMemberId();

        boolean agreed = memberQueryService.getWebPushAgreed(memberId);
        boolean hasSubscription = subscriptionRepository.existsByMemberId(memberId);

        return ApiResponse.success(
                new WebPushStatusResponse(agreed, hasSubscription)
        );
    }
}