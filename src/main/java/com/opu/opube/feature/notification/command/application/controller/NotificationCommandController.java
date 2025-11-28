package com.opu.opube.feature.notification.command.application.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.notification.command.application.dto.request.NotificationSettingRequest;
import com.opu.opube.feature.notification.command.application.service.NotificationCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
@Tag(
        name = "Notification - Command",
        description = "알림 읽음 처리 및 알림 설정 변경 API"
)
public class NotificationCommandController {

    private final NotificationCommandService notificationCommandService;

    @Operation(
            summary = "단일 알림 읽음 처리",
            description = """
                    지정한 알림(notificationId)을 읽음 처리합니다.
                    - 자신의 알림이 아닌 경우 404(Not Found)가 반환됩니다.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "읽음 처리 성공"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "알림을 찾을 수 없음 (NOTIFICATION_NOT_FOUND)"
    )
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @AuthenticationPrincipal
            @Parameter(hidden = true) MemberPrincipal principal,

            @Parameter(
                    description = "읽음 처리할 알림 ID",
                    example = "123"
            )
            @PathVariable Long notificationId
    ) {
        Long memberId = principal.getMemberId();
        notificationCommandService.markAsRead(memberId, notificationId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(
            summary = "내 모든 알림 읽음 처리",
            description = """
                    현재 로그인한 사용자의 모든 알림을 일괄 읽음 처리합니다.
                    - 알림이 하나도 없어도 호출 시 항상 200 OK 를 반환합니다.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "전체 읽음 처리 성공"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자"
    )
    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal
            @Parameter(hidden = true) MemberPrincipal principal
    ) {
        Long memberId = principal.getMemberId();
        notificationCommandService.markAllAsRead(memberId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(
            summary = "알림 설정 변경",
            description = """
                    특정 알림 타입(code)에 대한 수신 여부를 변경합니다.\n
                    - code 예시: FOLLOW, COMMENT, TODO_REMINDER 등\n
                    - body의 enabled 값에 따라 ON/OFF 설정을 변경합니다.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "알림 설정 변경 성공"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "존재하지 않는 알림 코드 (NOTIFICATION_TYPE_NOT_FOUND)"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자"
    )
    @PatchMapping("/settings/{code}")
    public ResponseEntity<ApiResponse<Void>> updateSetting(
            @AuthenticationPrincipal
            @Parameter(hidden = true) MemberPrincipal principal,

            @Parameter(
                    description = "알림 타입 코드 (예: FOLLOW, COMMENT, TODO_REMINDER)",
                    example = "TODO_REMINDER"
            )
            @PathVariable String code,

            @RequestBody(
                    description = "알림 활성/비활성 설정 요청 바디",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = NotificationSettingRequest.class)
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody NotificationSettingRequest request
    ) {
        Long memberId = principal.getMemberId();
        notificationCommandService.updateSetting(memberId, code, request.getEnabled());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}