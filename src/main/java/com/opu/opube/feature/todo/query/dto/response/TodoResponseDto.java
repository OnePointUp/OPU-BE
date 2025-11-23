package com.opu.opube.feature.todo.query.dto.response;

import com.opu.opube.feature.todo.command.domain.aggregate.Todo;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class TodoResponseDto {
    Long id;
    Long routineId;
    Long opuId;
    String title;
    LocalDate scheduledDate;
    LocalTime scheduledTime;
    Integer sortOrder;
    boolean completed;

    public static TodoResponseDto fromEntity(Todo todo) {
        return TodoResponseDto.builder()
                .id(todo.getId())
                .routineId(todo.getRoutine() != null ? todo.getRoutine().getId() : null)
                .opuId(todo.getOpu() != null ? todo.getOpu().getId() : null)
                .title(todo.getTitle())
                .scheduledDate(todo.getScheduledDate())
                .scheduledTime(todo.getScheduledTime())
                .completed(todo.isCompleted())
                .sortOrder(todo.getSortOrder())
                .build();
    }
}
