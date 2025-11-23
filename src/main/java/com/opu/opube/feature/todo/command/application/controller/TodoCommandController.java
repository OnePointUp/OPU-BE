package com.opu.opube.feature.todo.command.application.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.todo.command.application.dto.request.TodoCreateDto;
import com.opu.opube.feature.todo.command.application.dto.request.TodoStatusUpdateDto;
import com.opu.opube.feature.todo.command.application.dto.request.TodoUpdateDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.opu.opube.feature.todo.command.application.service.TodoCommandService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/debug/todo")
public class TodoCommandController {

    private final TodoCommandService todoCommandService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createTodo(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Valid @RequestBody TodoCreateDto todoCreateDto
            ) {
        Long memberId = principal.getMemberId();
        Long todoId = todoCommandService.createTodo(memberId, todoCreateDto);
        return ResponseEntity.ok(ApiResponse.success(todoId));
    }

    @PatchMapping("{todoId}")
    public ResponseEntity<ApiResponse<Void>> updateTodo(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long todoId,
            @RequestBody TodoUpdateDto dto
    ) {
        Long memberId = 1L; //todo : principal.getMemberId();
        todoCommandService.updateTodo(memberId, dto, todoId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/{todoId}/status")
    public ResponseEntity<ApiResponse<Void>> updateTodo(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long todoId,
            @Valid @RequestBody TodoStatusUpdateDto dto
    ) {
        Long memberId = 1L; //todo : principal.getMemberId();
        todoCommandService.updateStatus(memberId, dto, todoId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}