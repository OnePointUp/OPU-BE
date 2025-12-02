package com.opu.opube.feature.todo.query.dto.response;

import com.opu.opube.feature.todo.command.domain.aggregate.Frequency;
import com.opu.opube.feature.todo.command.domain.aggregate.Routine;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public class RoutineDetailResponseDto {
    Long id;
    String title;
    LocalDate startDate;
    LocalDate endDate;
    Frequency frequency;
    String weekDays;
    String monthDays;
    String days;

    public static RoutineDetailResponseDto fromEntity(Routine routine) {
        return RoutineDetailResponseDto.builder()
                .id(routine.getId())
                .title(routine.getTitle())
                .startDate(routine.getStartDate())
                .endDate(routine.getEndDate())
                .frequency(routine.getFrequency())
                .weekDays(routine.getWeekDays())
                .monthDays(routine.getMonthDays())
                .days(routine.getDays())
                .build();
    }
}
