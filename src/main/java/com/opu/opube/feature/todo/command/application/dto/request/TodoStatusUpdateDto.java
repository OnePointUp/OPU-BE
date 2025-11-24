package com.opu.opube.feature.todo.command.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class TodoStatusUpdateDto {
    @NotNull
    private Boolean completed;
}
