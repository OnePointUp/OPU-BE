package com.opu.opube.feature.todo.command.application.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.exception.ApiErrorResponses;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.todo.command.application.dto.request.RoutineCreateDto;
import com.opu.opube.feature.todo.command.application.dto.request.RoutineScope;
import com.opu.opube.feature.todo.command.application.dto.request.RoutineUpdateDto;
import com.opu.opube.feature.todo.command.application.service.RoutineCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/routine")
@Tag(name = "Routine Command API", description = "routine 등록/수정/삭제 API")
@Validated
public class RoutineCommandController {

    private final RoutineCommandService routineCommandService;

    @Operation(
            summary = "routine 생성",
            description = "routine 을 생성합니다."
    )
    @ApiErrorResponses(value = {ErrorCode.MEMBER_NOT_FOUND})
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createRoutine(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Valid @RequestBody RoutineCreateDto routineCreateDto
            ) {
        Long memberId = principal.getMemberId();
        Long routineId = routineCommandService.createRoutine(memberId, routineCreateDto);
        return ResponseEntity.ok(ApiResponse.success(routineId));
    }

    @Operation(
            summary = "routine 수정",
            description = "routine 을 수정합니다."
    )
    @ApiErrorResponses(value = {ErrorCode.MEMBER_NOT_FOUND, ErrorCode.ROUTINE_NOT_FOUND,
            ErrorCode.ROUTINE_FORBIDDEN})
    @PatchMapping("/{routineId}")
    public ResponseEntity<ApiResponse<Void>> updateRoutine(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long routineId,
            @Valid @RequestBody RoutineUpdateDto dto
    ) {
        Long memberId = principal.getMemberId();
        routineCommandService.updateRoutine(memberId, dto, routineId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(
            summary = "routine 삭제",
            description = "routine 을 삭제합니다."
    )
    @ApiErrorResponses(value = {ErrorCode.MEMBER_NOT_FOUND, ErrorCode.ROUTINE_NOT_FOUND,
            ErrorCode.ROUTINE_FORBIDDEN})
    @DeleteMapping("/{routineId}")
    public ResponseEntity<ApiResponse<Void>> deleteRoutine(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long routineId,
            @Schema(description = "삭제되는 루틴에 속한 todo 삭제 정책", example = "ALL", requiredMode = Schema.RequiredMode.REQUIRED)
            @Valid @NotNull(message = "루틴 삭제 정책은 필수입니다.") @RequestParam RoutineScope scope
    ) {
        Long memberId = principal.getMemberId();
        routineCommandService.deleteRoutine(memberId, routineId, scope);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}