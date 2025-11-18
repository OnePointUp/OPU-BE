package com.opu.opube.feature.notification.command.domain.aggregate;

import lombok.Getter;

@Getter
public enum NotificationTypeCode {
    ALL("ALL"),
    MORNING("MORNING"),
    EVENING("EVENING"),
    ROUTINE("ROUTINE"),
    RANDOM_PICK("RANDOM_PICK");

    private final String code;

    NotificationTypeCode(String code) {
        this.code = code;
    }

}