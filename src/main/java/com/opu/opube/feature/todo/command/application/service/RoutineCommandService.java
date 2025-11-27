package com.opu.opube.feature.todo.command.application.service;

import com.opu.opube.feature.todo.command.application.dto.request.*;
import jakarta.validation.Valid;

public interface RoutineCommandService {
    Long createRoutine(Long memberId, RoutineCreateDto routineCreateDto);

    // command methods here
}