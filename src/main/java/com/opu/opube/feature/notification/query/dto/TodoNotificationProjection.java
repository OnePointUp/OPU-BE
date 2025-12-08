package com.opu.opube.feature.notification.query.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public interface TodoNotificationProjection {

    Long getMemberId();
    Long getTodoId();
    String getTitle();
    LocalDate getScheduledDate();
    LocalTime getScheduledTime();
}