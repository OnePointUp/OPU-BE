package com.opu.opube.feature.notification.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class NotificationSettingRequest {

    @Schema(
            description = "알림 활성화 여부 (true = 켜기, false = 끄기)",
            example = "true"
    )
    private Boolean enabled;
}