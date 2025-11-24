package com.opu.opube.feature.todo.command.application.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.todo.command.application.dto.request.OpuTodoCreateDto;
import com.opu.opube.feature.todo.command.application.dto.request.TodoCreateDto;
import com.opu.opube.feature.todo.command.application.dto.request.TodoStatusUpdateDto;
import com.opu.opube.feature.todo.command.application.dto.request.TodoUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.opu.opube.feature.todo.command.application.service.TodoCommandService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Validated
@Tag(name = "Todo API", description = "Todo 등록/수정/삭제 관련 API")
public class TodoCommandController {

    private final TodoCommandService todoCommandService;

    @Operation(
            summary = "Todo 생성",
            description = "Todo를 직접 생성합니다."
    )
    @PostMapping("/todo")
    public ResponseEntity<ApiResponse<Long>> createTodo(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Valid @RequestBody TodoCreateDto todoCreateDto
            ) {
        Long memberId = principal.getMemberId();
        Long todoId = todoCommandService.createTodo(memberId, todoCreateDto);
        return ResponseEntity.ok(ApiResponse.success(todoId));
    }

    @Operation(
            summary = "OPU 에서 Todo 생성",
            description = "OPU id를 통해 Todo를 생성합니다."
    )
    @PostMapping("/opu/{opuId}/todo")
    public ResponseEntity<ApiResponse<Long>> createTodoByOpuId(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long opuId,
            @Valid @RequestParam OpuTodoCreateDto opuTodoCreateDto
    ) {
        Long memberId = principal.getMemberId();
        Long todoId = todoCommandService.createTodoByOpu(memberId, opuId, opuTodoCreateDto);
        return ResponseEntity.ok(ApiResponse.success(todoId));
    }

    @Operation(
            summary = "Todo 수정",
            description = "해당 todo 의 title, date, time 을 수정합니다."
    )
    @PatchMapping("/todo/{todoId}")
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
            summary = "Todo 완료 / 미완료 여부 수정",
            description = "해당 todo 의 완료 여부를 갱신합니다."
    )
    @PatchMapping("/todo/{todoId}/status")
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
            summary = "Todo 정렬 수정",
            description = "해당 todo의 정렬을 수정합니다."
    )
    @PatchMapping("/todo/{todoId}/order")
    public ResponseEntity<ApiResponse<Void>> reorderTodo(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long todoId,
            @RequestParam("newOrder") @Min(0) int newOrder
    ) {
        Long memberId = principal.getMemberId();
        todoCommandService.reorderTodo(memberId, newOrder, todoId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(
            summary = "Todo 삭제",
            description = "해당 todo를 삭제합니다."
    )
    @DeleteMapping("/todo/{todoId}")
    public ResponseEntity<ApiResponse<Void>> deleteTodo(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long todoId
    ) {
        Long memberId = principal.getMemberId();
        todoCommandService.deleteTodo(memberId, todoId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}