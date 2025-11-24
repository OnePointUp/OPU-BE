package com.opu.opube.feature.todo.command.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class OpuTodoCreateDto {
    @NotNull(message = "날짜는 필수입니다.")
    LocalDate scheduledDate;
    LocalTime scheduledTime;
}
