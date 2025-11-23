package com.opu.opube.feature.todo.query.infrastructure.repository;

import java.time.LocalDate;
import java.util.List;

import com.opu.opube.common.dto.PageResponse;
import com.opu.opube.feature.todo.command.domain.aggregate.Todo;
import com.opu.opube.feature.todo.query.dto.response.TodoResponseDto;

public interface TodoQueryRepository {
    public PageResponse<TodoResponseDto> getTodoByUserAndDate(Long memberId, LocalDate date, int page, int size);
}