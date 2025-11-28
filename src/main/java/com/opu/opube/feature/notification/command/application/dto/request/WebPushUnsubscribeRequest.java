package com.opu.opube.feature.notification.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WebPushUnsubscribeRequest {

    @Schema(
            description = "브라우저 Push Service Endpoint URL. 이 endpoint 를 기준으로 구독을 삭제합니다.",
            example = "https://fcm.googleapis.com/fcm/send/abc123:def456"
    )
    private String endpoint;
}