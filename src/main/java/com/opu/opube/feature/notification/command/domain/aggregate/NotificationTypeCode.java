package com.opu.opube.feature.notification.command.domain.aggregate;

import lombok.Getter;

@Getter
public enum NotificationTypeCode {
    ALL("ALL"),
    MORNING("MORNING"),
    EVENING("EVENING"),
    ROUTINE("ROUTINE"),
    TODO("TODO"),
    RANDOM_PICK("RANDOM_DRAW");

    private final String code;

    NotificationTypeCode(String code) {
        this.code = code;
    }

}