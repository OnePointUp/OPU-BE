package com.opu.opube.feature.todo.query.dto.response;

import com.opu.opube.feature.todo.command.domain.aggregate.Frequency;
import com.opu.opube.feature.todo.command.domain.aggregate.Routine;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class RoutineListResponseDto {
    Long id;
    LocalDate startDate;
    LocalDate endDate;
    String title;
    Frequency frequency;

    public static RoutineListResponseDto fromEntity(Routine routine) {
        return RoutineListResponseDto.builder()
                .id(routine.getId())
                .startDate(routine.getStartDate())
                .endDate(routine.getEndDate())
                .title(routine.getTitle())
                .frequency(routine.getFrequency())
                .build();
    }
}
