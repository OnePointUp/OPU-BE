package com.opu.opube.feature.todo.command.application.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.exception.ApiErrorResponses;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.todo.command.application.dto.request.OpuTodoCreateDto;
import com.opu.opube.feature.todo.command.application.dto.request.TodoCreateDto;
import com.opu.opube.feature.todo.command.application.dto.request.TodoStatusUpdateDto;
import com.opu.opube.feature.todo.command.application.dto.request.TodoUpdateDto;
import com.opu.opube.feature.todo.command.application.service.TodoCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Todo Command API", description = "todo 등록/수정/삭제 API")
@Validated
public class TodoCommandController {

    private final TodoCommandService todoCommandService;

    @Operation(
            summary = "todo 생성",
            description = "todo를 생성합니다."
    )
    @ApiErrorResponses(value = {ErrorCode.MEMBER_NOT_FOUND})
    @PostMapping("/todos")
    public ResponseEntity<ApiResponse<Long>> createTodo(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Valid @RequestBody TodoCreateDto todoCreateDto
            ) {
        Long memberId = principal.getMemberId();
        Long todoId = todoCommandService.createTodo(memberId, todoCreateDto);
        return ResponseEntity.ok(ApiResponse.success(todoId));
    }

    @Operation(
            summary = "opu 로부터 todo 생성",
            description = "opu 로부터 todo를 생성합니다."
    )
    @ApiErrorResponses(value = {ErrorCode.MEMBER_NOT_FOUND, ErrorCode.OPU_NOT_FOUND})
    @PostMapping("/opus/{opuId}/todos")
    public ResponseEntity<ApiResponse<Long>> createTodoByOpuId(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long opuId,
            @Valid @RequestBody OpuTodoCreateDto opuTodoCreateDto
    ) {
        Long memberId = principal.getMemberId();
        Long todoId = todoCommandService.createTodoByOpu(memberId, opuId, opuTodoCreateDto);
        return ResponseEntity.ok(ApiResponse.success(todoId));
    }

    @Operation(
            summary = "todo 수정",
            description = "todo를 수정합니다."
    )
    @ApiErrorResponses(value = {ErrorCode.MEMBER_NOT_FOUND, ErrorCode.TODO_NOT_FOUND,
            ErrorCode.TODO_FORBIDDEN, ErrorCode.OPU_TODO_CANNOT_BE_MODIFIED})
    @PatchMapping("/todos/{todoId}")
    public ResponseEntity<ApiResponse<Void>> updateTodo(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long todoId,
            @RequestBody TodoUpdateDto dto
    ) {
        Long memberId = principal.getMemberId();
        todoCommandService.updateTodo(memberId, dto, todoId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(
            summary = "todo 완료 / 미완료",
            description = "todo를 완료 / 미완료 할 수 있습니다."
    )
    @ApiErrorResponses(value = {ErrorCode.MEMBER_NOT_FOUND, ErrorCode.TODO_NOT_FOUND,
            ErrorCode.TODO_FORBIDDEN})
    @PatchMapping("/todos/{todoId}/status")
    public ResponseEntity<ApiResponse<Void>> updateTodo(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long todoId,
            @Valid @RequestBody TodoStatusUpdateDto dto
    ) {
        Long memberId = principal.getMemberId();
        todoCommandService.updateStatus(memberId, dto, todoId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(
            summary = "todo 정렬 수정",
            description = "todo 정렬을 원하는 위치로 수정할 수 있습니다."
    )
    @ApiErrorResponses(value = {ErrorCode.MEMBER_NOT_FOUND, ErrorCode.TODO_NOT_FOUND,
            ErrorCode.TODO_FORBIDDEN})
    @PatchMapping("/todos/{todoId}/order")
    public ResponseEntity<ApiResponse<Void>> reorderTodo(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long todoId,
            @Schema(name = "새 순서") @RequestParam("newOrder") @Min(0) int newOrder
    ) {
        Long memberId = principal.getMemberId();
        todoCommandService.reorderTodo(memberId, newOrder, todoId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(
            summary = "todo 삭제",
            description = "해당 todo를 삭제 할 수 있습니다."
    )
    @ApiErrorResponses(value = {ErrorCode.MEMBER_NOT_FOUND, ErrorCode.TODO_NOT_FOUND,
            ErrorCode.TODO_FORBIDDEN})
    @DeleteMapping("/todos/{todoId}")
    public ResponseEntity<ApiResponse<Void>> deleteTodo(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long todoId
    ) {
        Long memberId = principal.getMemberId();
        todoCommandService.deleteTodo(memberId, todoId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}