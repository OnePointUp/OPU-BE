package com.opu.opube.feature.todo.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Schema(title = "todo 수정 dto", description = "내용, 날짜, 시간 변경용 dto")
public class TodoUpdateDto {
    @Schema(description = "내용", example = "코딩 두시간 하기", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String title;
    @Schema(description = "날짜", example = "2025-12-12", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    LocalDate scheduledDate;
    @Schema(description = "시간", example = "22:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    LocalTime scheduledTime;
}
