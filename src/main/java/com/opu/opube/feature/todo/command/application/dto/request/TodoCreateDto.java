package com.opu.opube.feature.todo.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Schema(description = "TODO 추가 DTO")
public class TodoCreateDto {
    @Schema(description = "내용", example = "코딩 두시간 하기", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "제목은 필수입니다.")
    String title;
    @Schema(description = "날짜", example = "2025-12-12", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "날짜는 필수입니다.")
    LocalDate scheduledDate;
    @Schema(description = "시간", example = "22:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    LocalTime scheduledTime;
}
