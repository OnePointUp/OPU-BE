package com.opu.opube.feature.todo.command.application.service;

import com.opu.opube.feature.todo.command.application.dto.request.TodoCreateDto;

public interface TodoCommandService {
    Long createTodo(Long memberId, TodoCreateDto todoCreateDto);
    // command methods here
}