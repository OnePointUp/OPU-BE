package com.opu.opube.feature.notification.command.application.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.notification.command.application.dto.request.WebPushSubscribeRequest;
import com.opu.opube.feature.notification.command.application.dto.request.WebPushUnsubscribeRequest;
import com.opu.opube.feature.notification.command.application.service.WebPushNotificationService;
import com.opu.opube.feature.notification.command.application.service.WebPushSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications/push")
@RequiredArgsConstructor
public class NotificationWebPushController {

    private final WebPushSubscriptionService webPushSubscriptionService;
    private final WebPushNotificationService webPushNotificationService;

    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<Void>> subscribe(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestBody WebPushSubscribeRequest req
    ) {
        Long memberId = principal.getMemberId();
        webPushSubscriptionService.subscribe(memberId, req);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<ApiResponse<Void>> unsubscribe(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestBody WebPushUnsubscribeRequest req
    ) {
        Long memberId = principal.getMemberId();
        webPushSubscriptionService.unsubscribe(memberId, req);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/webpush/test")
    public ApiResponse<Void> sendTest(
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        Long memberId = principal.getMemberId();

        webPushNotificationService.sendToMember(
                memberId,
                "테스트 웹푸시",
                "이건 서버에서 보낸 테스트 웹푸시입니다."
        );

        return ApiResponse.success(null);
    }
}