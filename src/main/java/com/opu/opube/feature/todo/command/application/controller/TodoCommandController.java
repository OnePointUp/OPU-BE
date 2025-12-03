package com.opu.opube.feature.todo.command.application.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.todo.command.application.dto.request.OpuTodoCreateDto;
import com.opu.opube.feature.todo.command.application.dto.request.TodoCreateDto;
import com.opu.opube.feature.todo.command.application.dto.request.TodoStatusUpdateDto;
import com.opu.opube.feature.todo.command.application.dto.request.TodoUpdateDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.opu.opube.feature.todo.command.application.service.TodoCommandService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Validated
public class TodoCommandController {

    private final TodoCommandService todoCommandService;

    @PostMapping("/todo")
    public ResponseEntity<ApiResponse<Long>> createTodo(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Valid @RequestBody TodoCreateDto todoCreateDto
            ) {
        Long memberId = principal.getMemberId();
        Long todoId = todoCommandService.createTodo(memberId, todoCreateDto);
        return ResponseEntity.ok(ApiResponse.success(todoId));
    }

    @PostMapping("/opu/{opuId}/todo")
    public ResponseEntity<ApiResponse<Long>> createTodoByOpuId(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long opuId,
            @Valid @RequestBody OpuTodoCreateDto opuTodoCreateDto
    ) {
        Long memberId = principal.getMemberId();
        Long todoId = todoCommandService.createTodoByOpu(memberId, opuId, opuTodoCreateDto);
        return ResponseEntity.ok(ApiResponse.success(todoId));
    }

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