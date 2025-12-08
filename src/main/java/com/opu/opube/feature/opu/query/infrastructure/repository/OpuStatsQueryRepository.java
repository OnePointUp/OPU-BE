package com.opu.opube.feature.opu.query.infrastructure.repository;

import com.opu.opube.feature.opu.query.dto.response.OpuDailyStatsResponse;
import com.opu.opube.feature.opu.query.dto.response.OpuMonthlyStatsResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface OpuStatsQueryRepository {
    long countCompletedDays(Long memberId, LocalDateTime start, LocalDateTime end);
    long countCompletedOpus(Long memberId, LocalDateTime start, LocalDateTime end);
    long countRandomDraws(Long memberId, LocalDateTime start, LocalDateTime end);
    List<OpuMonthlyStatsResponse.TopCompletedOpu> findTopCompletedOpus(Long memberId, LocalDateTime start, LocalDateTime end, int limit);
    List<OpuDailyStatsResponse.DayStat> findDailyCompletedCounts(
            Long memberId,
            LocalDateTime start,
            LocalDateTime end
    );
}