package com.opu.opube.feature.notification.command.application.controller;

import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.notification.command.application.service.NotificationSseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(
        name = "Notification - SSE",
        description = "실시간 알림 수신을 위한 SSE 연결 API"
)
public class NotificationSseController {

    private final NotificationSseService notificationSseService;

    @Operation(
            summary = "알림 SSE 구독",
            description = """
                    클라이언트와 서버 간 SSE(Server-Sent Events) 연결을 생성합니다.\n
                    - 헤더에 JWT를 포함한 상태로 호출해야 합니다.\n
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "SSE 연결 성공",
            content = @Content(
                    mediaType = MediaType.TEXT_EVENT_STREAM_VALUE,
                    schema = @Schema(implementation = String.class)
            )
    )
    @ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자"
    )
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(
            @AuthenticationPrincipal
            @Parameter(hidden = true) MemberPrincipal principal
    ) {
        Long memberId = principal.getMemberId();
        return notificationSseService.connect(memberId);
    }
}