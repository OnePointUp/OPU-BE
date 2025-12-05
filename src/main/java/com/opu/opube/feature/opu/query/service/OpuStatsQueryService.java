package com.opu.opube.feature.opu.query.service;

import com.opu.opube.feature.opu.query.dto.response.OpuDailyStatsResponse;
import com.opu.opube.feature.opu.query.dto.response.OpuMonthlyStatsResponse;
import com.opu.opube.feature.opu.query.dto.response.OpuMonthlyStatsResponse.TopCompletedOpu;
import com.opu.opube.feature.opu.query.infrastructure.repository.OpuStatsQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OpuStatsQueryService {

    private final OpuStatsQueryRepository opuStatsQueryRepository;

    public OpuMonthlyStatsResponse getMonthlyStats(Long memberId, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.plusMonths(1).atDay(1).atStartOfDay();

        long completedDayCount =
                opuStatsQueryRepository.countCompletedDays(memberId, start, end);

        long completedOpuCount =
                opuStatsQueryRepository.countCompletedOpus(memberId, start, end);

        long randomDrawCount =
                opuStatsQueryRepository.countRandomDraws(memberId, start, end);

        List<TopCompletedOpu> topCompletedOpus =
                opuStatsQueryRepository.findTopCompletedOpus(memberId, start, end, 3);

        return OpuMonthlyStatsResponse.builder()
                .year(year)
                .month(month)
                .completedDayCount(completedDayCount)
                .completedOpuCount(completedOpuCount)
                .randomDrawCount(randomDrawCount)
                .topCompletedOpus(topCompletedOpus)
                .build();
    }

    public OpuDailyStatsResponse getDailyStats(Long memberId, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);

        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().plusDays(1).atStartOfDay();

        List<OpuDailyStatsResponse.DayStat> days =
                opuStatsQueryRepository.findDailyCompletedCounts(memberId, start, end);

        return new OpuDailyStatsResponse(year, month, days);
    }
}