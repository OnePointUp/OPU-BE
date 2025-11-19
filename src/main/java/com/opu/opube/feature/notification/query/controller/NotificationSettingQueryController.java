package com.opu.opube.feature.notification.query.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.notification.query.service.NotificationSettingQueryService;
import com.opu.opube.feature.notification.query.dto.NotificationSettingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications/settings")
@RequiredArgsConstructor
public class NotificationSettingQueryController {

    private final NotificationSettingQueryService notificationSettingQueryService;

    @GetMapping
    public ApiResponse<List<NotificationSettingResponse>> getMyNotificationSettings(
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        Long memberId = principal.getMemberId();
        List<NotificationSettingResponse> res =
                notificationSettingQueryService.getMySettings(memberId);

        return ApiResponse.success(res);
    }
}