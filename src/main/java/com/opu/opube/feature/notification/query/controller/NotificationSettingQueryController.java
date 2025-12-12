package com.opu.opube.feature.notification.query.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.notification.query.dto.NotificationSettingListResponse;
import com.opu.opube.feature.notification.query.dto.NotificationSettingResponse;
import com.opu.opube.feature.notification.query.service.NotificationSettingQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications/settings")
@RequiredArgsConstructor
@Tag(
        name = "Notification - Setting Query",
        description = "알림 설정 조회 관련 API"
)
public class NotificationSettingQueryController {

    private final NotificationSettingQueryService notificationSettingQueryService;

    @Operation(
            summary = "나의 알림 설정 목록 조회",
            description = """
                    로그인한 사용자의 알림 설정 목록을 조회합니다.
                    - 알림 타입별로 on/off 여부를 반환합니다.
                    """,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "알림 설정 조회 성공",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = NotificationSettingResponse.class)
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자 (UNAUTHORIZED_USER)"
            )
    })
    @GetMapping("/settings")
    public ApiResponse<NotificationSettingListResponse> getMyNotificationSettings(
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        Long memberId = principal.getMemberId();
        return ApiResponse.success(notificationSettingQueryService.getMySettings(memberId));
    }
}