package com.opu.opube.feature.notification.query.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TodoNotificationResponse implements TodoNotificationProjection {

    private Long memberId;
    private Long todoId;
    private String title;
    private LocalDate scheduledDate;
    private LocalTime scheduledTime;
}