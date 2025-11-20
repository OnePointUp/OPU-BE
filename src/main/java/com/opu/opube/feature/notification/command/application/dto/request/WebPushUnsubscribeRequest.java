package com.opu.opube.feature.notification.command.application.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WebPushUnsubscribeRequest {

    private String endpoint;
}