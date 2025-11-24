package com.opu.opube.feature.todo.command.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class TodoCreateDto {
    @NotNull(message = "제목은 필수입니다.")
    String title;
    @NotNull(message = "날짜는 필수입니다.")
    LocalDate scheduledDate;
    LocalTime scheduledTime;
}
