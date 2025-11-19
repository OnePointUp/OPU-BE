package com.opu.opube.feature.notification.query.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.common.dto.PageResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.notification.command.application.dto.response.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.opu.opube.feature.notification.query.service.NotificationQueryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationQueryController {

    private final NotificationQueryService notificationQueryService;

    @GetMapping
    public ApiResponse<PageResponse<NotificationResponse>> getMyNotifications(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestParam(defaultValue = "false") boolean onlyUnread,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Long memberId = principal.getMemberId();
        PageRequest pageable = PageRequest.of(page, size);

        PageResponse<NotificationResponse> res =
                notificationQueryService.getMyNotifications(memberId, onlyUnread, pageable);

        return ApiResponse.success(res);
    }

    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadCount(
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        long count = notificationQueryService.getUnreadCount(principal.getMemberId());
        return ApiResponse.success(count);
    }
}