package com.opu.opube.feature.notification.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WebPushSubscribeRequest {

    @Schema(
            description = "브라우저가 생성한 Push Service Endpoint URL",
            example = "https://fcm.googleapis.com/fcm/send/abc123:def456"
    )
    private String endpoint;

    @Schema(
            description = "웹푸시 암호화에 사용되는 클라이언트 공개키(p256dh)",
            example = "BLa9xYx8kPxjmhNTq7GdTrCfwJm1mQfT6jV0jE7qYWZc="
    )
    private String p256dh;

    @Schema(
            description = "웹푸시 암호화에 사용되는 인증키(auth)",
            example = "bXlBdXRoS2V5MTIz"
    )
    private String auth;

    @Schema(
            description = "구독 만료 시간(Unix milliseconds). null이면 무기한.",
            example = "1715500000000",
            nullable = true
    )
    private Long expirationTime;
}