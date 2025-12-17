package com.opu.opube.feature.todo.query.infrastructure.repository;

import java.time.LocalDate;
import java.util.List;

import com.opu.opube.common.dto.PageResponse;
import com.opu.opube.feature.todo.command.domain.aggregate.Todo;
import com.opu.opube.feature.todo.query.dto.response.*;

public interface TodoQueryRepository {
    public PageResponse<TodoResponseDto> getTodoByUserAndDate(Long memberId, LocalDate date, int page, int size);

    TodoMonthResponse getTodosInMonth(Long memberId, int year, int month);

    List<TodoStatisticsDto> findStatisticsByDateRange(Long memberId, LocalDate startDate, LocalDate endDate);

    List<DayTodoStats> getRoutineTodo(Long memberId, Long routineId, LocalDate start, LocalDate end);

    List<TodoStatRow> getAllRoutineTodo(Long memberId, List<Long> routineIds, LocalDate start, LocalDate end);
}