package com.opu.opube.feature.todo.command.application.dto.request;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class TodoUpdateDto {
    String title;
    LocalDate scheduledDate;
    LocalTime scheduledTime;
}
