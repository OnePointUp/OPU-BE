package com.opu.opube.feature.notification.command.application.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.notification.command.application.dto.request.NotificationSettingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.opu.opube.feature.notification.command.application.service.NotificationCommandService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationCommandController {

    private final NotificationCommandService notificationCommandService;

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long notificationId
    ) {
        Long memberId = principal.getMemberId();
        notificationCommandService.markAsRead(memberId, notificationId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        Long memberId = principal.getMemberId();
        notificationCommandService.markAllAsRead(memberId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/settings/{code}")
    public ResponseEntity<ApiResponse<Void>> updateSetting(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable String code,
            @RequestBody NotificationSettingRequest request
    ) {
        Long memberId = principal.getMemberId();
        notificationCommandService.updateSetting(memberId, code, request.getEnabled());
        return ResponseEntity.ok(ApiResponse.success(null));
    }


}