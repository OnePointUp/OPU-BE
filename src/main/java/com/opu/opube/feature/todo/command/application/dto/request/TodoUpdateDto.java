package com.opu.opube.feature.todo.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Schema(description = "Todo 수정 요청 DTO")
public class TodoUpdateDto {
    @Schema(description = "todo 이름", example = "통계 api 작성하기", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String title;
    @Schema(description = "예정 날짜", example = "2025-12-12", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    LocalDate scheduledDate;
    @Schema(description = "예정 시간", example = "23:59", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    LocalTime scheduledTime;
}
