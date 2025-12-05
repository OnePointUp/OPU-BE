package com.opu.opube.feature.opu.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "월별 일별 OPU 완료 횟수 응답")
public class OpuDailyStatsResponse {

    @Schema(description = "조회 연도", example = "2025")
    private int year;

    @Schema(description = "조회 월", example = "12")
    private int month;

    @Schema(description = "일별 OPU 완료 통계 리스트")
    private List<DayStat> days;

    @Getter
    @AllArgsConstructor
    @Schema(description = "특정 일자의 완료 통계")
    public static class DayStat {

        @Schema(description = "날짜", example = "2025-12-03")
        private LocalDate date;

        @Schema(description = "해당 날짜의 OPU 완료 횟수", example = "3")
        private long completedCount;
    }
}