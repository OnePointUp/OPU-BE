package com.opu.opube.feature.notification.query.dto;

import com.opu.opube.feature.todo.command.domain.aggregate.Frequency;
import java.time.LocalDate;

public record RoutineWeeklyProjection(
        Long memberId,
        Long routineId,
        String title,
        Frequency frequency,
        LocalDate startDate,
        LocalDate endDate,
        String weekDays,
        String monthDays,
        String yearDays
) {}