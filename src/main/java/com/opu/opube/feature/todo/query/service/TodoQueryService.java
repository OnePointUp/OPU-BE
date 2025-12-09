package com.opu.opube.feature.todo.query.service;

import com.opu.opube.common.dto.PageResponse;
import com.opu.opube.feature.todo.query.dto.response.*;
import com.opu.opube.feature.todo.query.infrastructure.repository.TodoQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
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

    public MonthlyRoutineTodoStatsResponse getRoutineStat(Long memberId, Long routineId, RoutineDetailResponseDto routine, int year, int month) {
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
                .title(routine.getTitle())
                .color(routine.getColor())
                .year(year)
                .month(month)
                .days(filledResults)
                .build();
    }

    public List<MonthlyAllRoutineTodoStatsResponse> getAllRoutineStat(Long memberId, List<RoutineStatResponseDto> content, int year, int month) {
        List<Long> routineIds = content.stream().map(RoutineStatResponseDto::getId).toList();

        if (routineIds.isEmpty()) {
            return List.of();
        }

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1);

        // todos bulk 조회 (routineId IN)
        List<TodoStatRow> rows = todoQueryRepository.getAllRoutineTodo(memberId, routineIds, start, end);

        // routineId -> (date -> TodoStatRow) 로 그룹핑
        Map<Long, Map<LocalDate, TodoStatRow>> routineDateMap = new HashMap<>();

        for (TodoStatRow row : rows) {
            routineDateMap
                    .computeIfAbsent(row.getRoutineId(), k -> new HashMap<>())
                    .put(row.getDate(), row);
        }

        // 5. routine 단위 응답 생성
        List<MonthlyAllRoutineTodoStatsResponse> responses = new ArrayList<>();

        for (RoutineStatResponseDto routine : content) {

            Map<LocalDate, TodoStatRow> dateMap =
                    routineDateMap.getOrDefault(routine.getId(), Map.of());

            List<TodoStatRow> days = new ArrayList<>();

            LocalDate current = start;
            while (current.isBefore(end)) {

                if (dateMap.containsKey(current)) {
                    // todos가 있었던 날 (DB 조회 결과)
                    days.add(dateMap.get(current));
                } else {
                    // todos가 없던 날 → 더미 데이터 생성
                    days.add(createEmptyDay(routine.getId(), current));
                }

                current = current.plusDays(1);
            }

            responses.add(
                    MonthlyAllRoutineTodoStatsResponse.builder()
                            .routineId(routine.getId())
                            .title(routine.getTitle())
                            .color(routine.getColor())
                            .year(year)
                            .month(month)
                            .days(days)
                            .build()
            );
        }

        return responses;
    }

    private TodoStatRow createEmptyDay(long routineId, LocalDate date) {
        return TodoStatRow.builder()
                .routineId(routineId)
                .hasTodo(false)
                .date(date)
                .build();
    }
}