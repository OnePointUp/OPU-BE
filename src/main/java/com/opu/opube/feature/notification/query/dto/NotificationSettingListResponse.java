package com.opu.opube.feature.notification.query.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "회원의 전체 알림 설정 목록 응답")
public class NotificationSettingListResponse {

    @Schema(
            description = "웹 푸시 알림 허용 여부",
            example = "true"
    )
    private Boolean webPushAgreed;

    @Schema(
            description = "개별 알림 타입 설정 목록",
            implementation = NotificationSettingResponse.class
    )
    private List<NotificationSettingResponse> settings;
}