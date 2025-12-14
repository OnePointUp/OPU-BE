package com.opu.opube.feature.notification.command.application.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.notification.command.application.service.NotificationCommandService;
import com.opu.opube.feature.notification.command.domain.aggregate.NotificationTypeCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dev/notifications")
@Tag(name = "Notification - Dev/Test", description = "개발/테스트용 알림 발송 API (local/dev only)")
@Profile({"local", "dev"})
public class NotificationTestController {

    private final NotificationCommandService notificationCommandService;

    @Operation(
            summary = "테스트 알림 발송 (본인에게)",
            description = "현재 로그인한 사용자에게 테스트 알림을 1건 발송합니다."
    )
    @PostMapping("/test")
    public ResponseEntity<ApiResponse<Void>> sendTestToMe(
            @AuthenticationPrincipal
            @Parameter(hidden = true) MemberPrincipal principal,

            @RequestParam(defaultValue = "TEST") String code,
            @RequestParam(defaultValue = "테스트 알림") String title,
            @RequestParam(defaultValue = "SSE/WebPush 확인용") String content
    ) {
        Long memberId = principal.getMemberId();

        notificationCommandService.sendNotification(memberId, NotificationTypeCode.MORNING, title, content, null);

        return ResponseEntity.ok(ApiResponse.success(null));
    }

}