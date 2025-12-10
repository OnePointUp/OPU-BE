package com.opu.opube.feature.todo.query.service;

import com.opu.opube.common.dto.PageResponse;
import com.opu.opube.feature.todo.query.dto.response.*;
import com.opu.opube.feature.todo.query.infrastructure.repository.RoutineQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoutineQueryService {

    private final RoutineQueryRepository routineQueryRepository;

    public PageResponse<RoutineListResponseDto> getRoutineList(Long memberId, int page, int size) {
        return routineQueryRepository.getRoutines(memberId, page, size);
    }

    public RoutineDetailResponseDto getRoutine(Long memberId, Long routineId) {
        return routineQueryRepository.getRoutine(memberId, routineId);
    }

    public PageResponse<RoutineSummaryResponseDto> getRoutineTitleList(Long memberId, int page, int size) {
        return routineQueryRepository.getRoutineTitle(memberId, page, size);
    }

    public PageResponse<RoutineStatResponseDto> getRoutineStatList(Long memberId, int page, int size) {
        return routineQueryRepository.getRoutineStatList(memberId, page, size);
    }

    @Transactional(readOnly = true)
    public RoutineMonthlyStatsResponse getRoutineStats(
            Long memberId,
            Long routineId,
            Integer year,
            Integer month
    ) {
        LocalDate now = LocalDate.now();
        int y = (year != null) ? year : now.getYear();
        int m = (month != null) ? month : now.getMonthValue();

        LocalDate start = LocalDate.of(y, m, 1);
        LocalDate end = YearMonth.of(y, m).atEndOfMonth();

        List<TodoStatRow> dailyList = routineQueryRepository.findDailyCompletion(memberId, routineId, start, end);

        long scheduledCount = dailyList.size();
        long completedCount = dailyList.stream()
                .filter(row -> Boolean.TRUE.equals(row.getDone()))
                .count();

        int achievementRate = 0;
        if (scheduledCount > 0) {
            achievementRate = (int) Math.round(
                    (completedCount * 100.0) / scheduledCount
            );
        }

        int streak = calcStreakDays(dailyList, y, m);

        return RoutineMonthlyStatsResponse.builder()
                .routineId(routineId)
                .year(y)
                .month(m)
                .achievementRate(achievementRate)
                .streakDays(streak)
                .completedCount(completedCount)
                .scheduledCount(scheduledCount)
                .build();
    }

    private int calcStreakDays(List<TodoStatRow> dailyList, int year, int intMonth) {
        LocalDate today = LocalDate.now();
        LocalDate effectiveEndDate;

        if (year == today.getYear() && intMonth == today.getMonthValue()) {
            effectiveEndDate = today;
        } else if (year < today.getYear() || (year == today.getYear() && intMonth < today.getMonthValue())) {
            effectiveEndDate = YearMonth.of(year, intMonth).atEndOfMonth();
        } else {
            return 0;
        }

        if (dailyList.isEmpty()) {
            return 0;
        }

        int streak = 0;
        for (TodoStatRow row : dailyList) {
            if (row.getDate().isAfter(effectiveEndDate)) {
                continue;
            }

            if (Boolean.TRUE.equals(row.getDone())) {
                streak++;
            } else {
                break;
            }
        }
        return streak;
    }
}
