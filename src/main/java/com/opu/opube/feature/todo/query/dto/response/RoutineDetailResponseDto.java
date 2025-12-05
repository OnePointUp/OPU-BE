package com.opu.opube.feature.todo.query.dto.response;

import com.opu.opube.feature.todo.command.domain.aggregate.Frequency;
import com.opu.opube.feature.todo.command.domain.aggregate.Routine;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Getter
public class RoutineDetailResponseDto {
    Long id;
    String title;
    LocalDate startDate;
    LocalDate endDate;
    Frequency frequency;
    String weekDays;
    String monthDays;
    String days;
    String color;
    LocalTime alarmTime;
    boolean active;

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
                .color(routine.getColor())
                .alarmTime(routine.getAlarmTime())
                .active(routine.isActive())
                .build();
    }
}
