package com.opu.opube.feature.todo.query.service;

import com.opu.opube.common.dto.PageResponse;
import com.opu.opube.feature.todo.query.dto.response.DayTodoStats;
import com.opu.opube.feature.todo.query.dto.response.MonthlyRoutineTodoStatsResponse;
import com.opu.opube.feature.todo.query.dto.response.TodoResponseDto;
import com.opu.opube.feature.todo.query.dto.response.TodoStatisticsDto;
import com.opu.opube.feature.todo.query.infrastructure.repository.TodoQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoQueryService {

    private final TodoQueryRepository todoQueryRepository;

    @Transactional(readOnly = true)
    public PageResponse<TodoResponseDto> getTodoByUserAndDate(Long memberId, LocalDate date, int page, int size) {
        return todoQueryRepository.getTodoByUserAndDate(memberId, date, page, size);
    }

    @Transactional(readOnly = true)
    public List<TodoStatisticsDto> getStatisticsByDateRange(Long memberId, LocalDate startDate, LocalDate endDate) {
        List<TodoStatisticsDto> results = todoQueryRepository.findStatisticsByDateRange(memberId, startDate, endDate);

        Map<LocalDate, TodoStatisticsDto> map = results.stream()
                .collect(Collectors.toMap(TodoStatisticsDto::getDate, Function.identity()));

        List<TodoStatisticsDto> filledResults = new ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            filledResults.add(
                    map.getOrDefault(current, new TodoStatisticsDto(current, 0, 0))
            );
            current = current.plusDays(1);
        }
        return filledResults;
    }

    public MonthlyRoutineTodoStatsResponse getRoutineStat(Long memberId, Long routineId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1);

        List<DayTodoStats> results = todoQueryRepository.getRoutineTodo(memberId, routineId, start, end);

        Map<LocalDate, DayTodoStats> map = results.stream()
                .collect(Collectors.toMap(DayTodoStats::getDate, Function.identity()));

        List<DayTodoStats> filledResults = new ArrayList<>();
        LocalDate current = start;
        while(current.isBefore(end)) {
            filledResults.add(
                    map.getOrDefault(current, new DayTodoStats(current, false, null))
            );
            current = current.plusDays(1);
        }

        return MonthlyRoutineTodoStatsResponse.builder()
                .routineId(routineId)
                .year(year)
                .month(month)
                .days(filledResults)
                .build();
    }
}