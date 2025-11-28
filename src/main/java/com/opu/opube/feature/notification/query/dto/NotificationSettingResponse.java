package com.opu.opube.feature.notification.query.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
@Schema(description = "알림 설정 정보 응답 DTO")
public class NotificationSettingResponse {

    @Schema(
            description = "알림 타입 ID",
            example = "3"
    )
    private Long typeId;

    @Schema(
            description = "알림 코드",
            example = "MORNING"
    )
    private String code;

    @Schema(
            description = "알림 타입 이름",
            example = "할 일 리마인더"
    )
    private String name;

    @Schema(
            description = "알림 설명",
            example = "매일 특정 시간에 할 일 리마인더 알림을 보내줍니다."
    )
    private String description;

    @Schema(
            description = "현재 알림 활성 여부 (true = 활성화됨)",
            example = "true"
    )
    private Boolean enabled;

    @Schema(
            description = "기본 알림 시간 (일부 알림 타입만 사용)",
            example = "09:00"
    )
    private LocalTime defaultTime;
}