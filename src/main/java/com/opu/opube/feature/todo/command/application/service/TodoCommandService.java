package com.opu.opube.feature.todo.command.application.service;

import com.opu.opube.feature.todo.command.application.dto.request.TodoCreateDto;
import com.opu.opube.feature.todo.command.application.dto.request.TodoUpdateDto;

public interface TodoCommandService {
    Long createTodo(Long memberId, TodoCreateDto todoCreateDto);

    void updateTodo(Long memberId, TodoUpdateDto dto);
    // command methods here
}