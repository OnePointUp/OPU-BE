package com.opu.opube.feature.todo.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Schema(description = "TODO 완료 여부 설정 dto")
public class TodoStatusUpdateDto {
    @Schema(description = "완료 여부", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Boolean completed;
}
