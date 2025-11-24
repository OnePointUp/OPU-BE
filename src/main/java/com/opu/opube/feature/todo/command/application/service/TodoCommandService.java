package com.opu.opube.feature.todo.command.application.service;

import com.opu.opube.feature.todo.command.application.dto.request.OpuTodoCreateDto;
import com.opu.opube.feature.todo.command.application.dto.request.TodoCreateDto;
import com.opu.opube.feature.todo.command.application.dto.request.TodoStatusUpdateDto;
import com.opu.opube.feature.todo.command.application.dto.request.TodoUpdateDto;

public interface TodoCommandService {
    Long createTodo(Long memberId, TodoCreateDto todoCreateDto);

    void updateTodo(Long memberId, TodoUpdateDto dto, Long todoId);

    void updateStatus(Long memberId, TodoStatusUpdateDto dto, Long todoId);

    void deleteTodo(Long memberId, Long todoId);

    void reorderTodo(Long memberId, int newOrder, Long todoId);

    Long createTodoByOpu(Long memberId, Long opuId, OpuTodoCreateDto opuTodoCreateDto);
    // command methods here
}