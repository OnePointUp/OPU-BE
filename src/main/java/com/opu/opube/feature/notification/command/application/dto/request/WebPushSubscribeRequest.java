package com.opu.opube.feature.notification.command.application.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WebPushSubscribeRequest {

    private String endpoint;
    private String p256dh;
    private String auth;
    private Long expirationTime;
}