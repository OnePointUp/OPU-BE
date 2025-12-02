package com.opu.opube.feature.todo.command.application.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.todo.command.application.dto.request.*;
import com.opu.opube.feature.todo.command.application.service.RoutineCommandService;
import com.opu.opube.feature.todo.command.application.service.TodoCommandService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/routine")
@Validated
public class RoutineCommandController {

    private final RoutineCommandService routineCommandService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createRoutine(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Valid @RequestBody RoutineCreateDto routineCreateDto
            ) {
        Long memberId = principal.getMemberId();
        Long routineId = routineCommandService.createRoutine(memberId, routineCreateDto);
        return ResponseEntity.ok(ApiResponse.success(routineId));
    }

    @PatchMapping("/{routineId}")
    public ResponseEntity<ApiResponse<Void>> updateRoutine(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long routineId,
            @RequestBody RoutineUpdateDto dto
    ) {
        Long memberId = principal.getMemberId();
        routineCommandService.updateRoutine(memberId, dto, routineId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
/*
    @DeleteMapping("/{routineId}")
    public ResponseEntity<ApiResponse<Void>> deleteRoutine(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long routineId
    ) {
        Long memberId = principal.getMemberId();
        routineCommandService.deleteRoutine(memberId, routineId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }*/
}