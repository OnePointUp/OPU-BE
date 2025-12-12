package com.opu.opube.feature.notification.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class NotificationSettingListResponse {
    private Boolean webPushAgreed;
    private List<NotificationSettingResponse> settings;
}