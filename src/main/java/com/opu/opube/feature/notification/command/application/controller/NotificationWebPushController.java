package com.opu.opube.feature.notification.command.application.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.notification.command.application.dto.request.WebPushSubscribeRequest;
import com.opu.opube.feature.notification.command.application.dto.request.WebPushUnsubscribeRequest;
import com.opu.opube.feature.notification.command.application.service.WebPushSubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications/push")
@RequiredArgsConstructor
@Tag(
        name = "Notification - Web Push",
        description = "브라우저 WebPush 구독/해제 및 테스트 발송 API"
)
public class NotificationWebPushController {

    private final WebPushSubscriptionService webPushSubscriptionService;

    @Operation(
            summary = "웹푸시 구독 등록",
            description = """
                    사용자의 브라우저에서 Web Push 구독 정보를 전달받아 서버에 저장합니다.\n
                    - endpoint / p256dh / auth 를 모두 전달해야 합니다.\n
                    - 동일 endpoint가 이미 존재하면 키 정보가 갱신됩니다.\n
                    - expirationTime은 브라우저마다 존재하지 않을 수 있습니다.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "구독 성공"
    )
    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<Void>> subscribe(
            @AuthenticationPrincipal
            @Parameter(hidden = true) MemberPrincipal principal,
            @RequestBody WebPushSubscribeRequest req
    ) {
        Long memberId = principal.getMemberId();
        webPushSubscriptionService.subscribe(memberId, req);
        return ResponseEntity.ok(ApiResponse.success(null));
    }


    @Operation(
            summary = "웹푸시 구독 해제",
            description = """
                    브라우저에서 전달받은 endpoint 기반으로\n
                    해당 사용자의 WebPush 구독 정보를 제거합니다.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "삭제 성공"
    )
    @PostMapping("/unsubscribe")
    public ResponseEntity<ApiResponse<Void>> unsubscribe(
            @AuthenticationPrincipal
            @Parameter(hidden = true) MemberPrincipal principal,
            @RequestBody WebPushUnsubscribeRequest req
    ) {
        Long memberId = principal.getMemberId();
        webPushSubscriptionService.unsubscribe(memberId, req);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}