package com.opu.opube.feature.todo.command.application.dto.request;

import com.opu.opube.feature.todo.command.application.dto.validator.RoutineUpdateConstraint;
import com.opu.opube.feature.todo.command.domain.aggregate.Frequency;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@RoutineUpdateConstraint
public class RoutineUpdateDto {
    String title;
    String color;
    LocalTime alarmTime;
    LocalDate endDate;
    Frequency frequency;
    String weekDays;
    String monthDays;
    String days;
    RoutineScope scope;
}
