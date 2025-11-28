package com.opu.opube.feature.notification.query.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.common.dto.PageResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.notification.command.application.dto.response.NotificationResponse;
import com.opu.opube.feature.notification.query.service.NotificationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
@Tag(
        name = "Notification - Query",
        description = "알림 조회 관련 API"
)
public class NotificationQueryController {

    private final NotificationQueryService notificationQueryService;

    @Operation(
            summary = "내 알림 목록 조회",
            description = """
                    로그인한 사용자의 알림 목록을 페이지네이션으로 조회합니다.
                    
                    - `onlyUnread = false` : 읽음/안 읽음 전체 알림 조회
                    - `onlyUnread = true`  : 아직 읽지 않은 알림만 조회
                    - `page` : 0부터 시작하는 페이지 번호
                    - `size` : 한 페이지에 가져올 알림 개수
                    """,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "알림 목록 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = NotificationResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자 (UNAUTHORIZED_USER)"
            )
    })
    @GetMapping
    public ApiResponse<PageResponse<NotificationResponse>> getMyNotifications(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Parameter(
                    description = "true: 읽지 않은 알림만 조회, false: 전체 알림 조회",
                    example = "false"
            )
            @RequestParam(defaultValue = "false") boolean onlyUnread,
            @Parameter(
                    description = "0부터 시작하는 페이지 번호",
                    example = "0"
            )
            @RequestParam(defaultValue = "0") int page,
            @Parameter(
                    description = "페이지당 알림 개수",
                    example = "20"
            )
            @RequestParam(defaultValue = "20") int size
    ) {
        Long memberId = principal.getMemberId();
        PageRequest pageable = PageRequest.of(page, size);

        PageResponse<NotificationResponse> res =
                notificationQueryService.getMyNotifications(memberId, onlyUnread, pageable);

        return ApiResponse.success(res);
    }

    @Operation(
            summary = "안 읽은 알림 개수 조회",
            description = """
                    로그인한 사용자의 읽지 않은 알림 개수를 조회합니다.
                    
                    - 헤더의 JWT 기준으로 현재 사용자 식별
                    - 반환 값은 읽지 않은 알림의 총 개수입니다.
                    """,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "안 읽은 알림 개수 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = Long.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자 (UNAUTHORIZED_USER)"
            )
    })
    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadCount(
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        long count = notificationQueryService.getUnreadCount(principal.getMemberId());
        return ApiResponse.success(count);
    }
}