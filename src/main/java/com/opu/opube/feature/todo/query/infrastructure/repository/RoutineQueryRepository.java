package com.opu.opube.feature.todo.query.infrastructure.repository;

import com.opu.opube.common.dto.PageResponse;
import com.opu.opube.feature.todo.query.dto.response.*;

import java.time.LocalDate;
import java.util.List;

public interface RoutineQueryRepository {
    PageResponse<RoutineListResponseDto> getRoutines(Long memberId, int page, int size);

    RoutineDetailResponseDto getRoutine(Long memberId, Long routineId);

    PageResponse<RoutineSummaryResponseDto> getRoutineTitle(Long memberId, int page, int size);

    PageResponse<RoutineStatResponseDto> getRoutineStatList(Long memberId, int page, int size);

    MonthlyRoutineStats getMonthlyRoutineStats(
            Long memberId, Long routineId, LocalDate startDate, LocalDate endDate
    );

    List<TodoStatRow> findDailyCompletion(
            Long memberId, Long routineId, LocalDate startDate, LocalDate endDate
    );
}