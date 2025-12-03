package com.opu.opube.feature.todo.command.application.service;

import com.opu.opube.feature.todo.command.application.dto.request.*;
import jakarta.validation.Valid;

public interface RoutineCommandService {
    Long createRoutine(Long memberId, RoutineCreateDto routineCreateDto);

    void updateRoutine(Long memberId, RoutineUpdateDto dto, Long routineId);

    void deleteRoutine(Long memberId, Long routineId, RoutineScope scope);

    // command methods here
}