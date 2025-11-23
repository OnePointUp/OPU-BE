package com.opu.opube.feature.todo.command.application.dto.request;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class TodoCreateDto {
    String title;
    LocalDate scheduledDate;
    LocalTime scheduledTime;
}
