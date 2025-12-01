package com.opu.opube.feature.notification.query.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WebPushStatusResponse {
    private boolean webPushAgreed;
    private boolean hasSubscription;
}