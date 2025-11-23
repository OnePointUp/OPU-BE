package com.opu.opube.feature.todo.command.application.dto.request;

import jakarta.validation.constraints.NotNull;

public class TodoStatusUpdateDto {
    @NotNull
    private Boolean completed;
}
