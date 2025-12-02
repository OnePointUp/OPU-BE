package com.opu.opube.feature.todo.command.application.dto.request;

import com.opu.opube.feature.todo.command.application.dto.validator.RoutineCreateConstraint;
import com.opu.opube.feature.todo.command.domain.aggregate.Frequency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@RoutineCreateConstraint
public class RoutineCreateDto {
    @NotNull()
    String title;
    String color;
    @NotNull()
    Frequency frequency;
    LocalDate startDate = LocalDate.now();
    @NotNull()
    LocalDate endDate;
    LocalTime alarmTime;
    @Schema(example = "0,1,2,3,4,5,6")
    String weekDays;
    @Schema(example = "1,10,31,L")
    String monthDays;
    @Schema(example = "1-10,2-20")
    String yearDays;
}
