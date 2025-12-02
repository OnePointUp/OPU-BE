package com.opu.opube.feature.todo.command.application.dto.request;

import com.opu.opube.feature.todo.command.application.dto.validator.RoutineUpdateConstraint;
import com.opu.opube.feature.todo.command.domain.aggregate.Frequency;
import lombok.Getter;

@Getter
@RoutineUpdateConstraint
public class RoutineUpdateDto {
    String title;
    String color;
    Frequency frequency;
    String weekDays;
    String monthDays;
    String days;
    RoutineUpdateScope scope;
}
