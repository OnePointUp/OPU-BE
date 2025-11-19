package com.opu.opube.feature.notification.query.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class NotificationSettingResponse {

    private Long typeId;
    private String code;
    private String name;
    private String description;
    private Boolean enabled;
    private LocalTime defaultTime;
}