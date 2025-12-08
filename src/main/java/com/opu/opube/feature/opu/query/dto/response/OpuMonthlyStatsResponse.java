package com.opu.opube.feature.opu.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "월간 OPU 통계 응답")
public class OpuMonthlyStatsResponse {

    @Schema(description = "조회 연도", example = "2025")
    private int year;

    @Schema(description = "조회 월", example = "12")
    private int month;

    @Schema(description = "해당 월 동안 OPU를 완료한 날 수(1개라도 수행한 날)", example = "12")
    private long completedDayCount;

    @Schema(description = "해당 월 동안 수행한 전체 OPU 완료 횟수", example = "31")
    private long completedOpuCount;

    @Schema(description = "해당 월 동안 랜덤 뽑기를 실행한 횟수", example = "8")
    private long randomDrawCount;

    @Schema(description = "가장 많이 완료한 OPU 리스트")
    private List<TopCompletedOpu> topCompletedOpus;

    @Getter
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    @Schema(description = "많이 완료된 OPU 정보")
    public static class TopCompletedOpu {

        @Schema(description = "OPU ID", example = "101")
        private Long opuId;

        @Schema(description = "OPU 제목", example = "아침 스트레칭 5분 하기")
        private String title;

        @Schema(description = "OPU 이모지", example = "💪")
        private String emoji;

        @Schema(description = "카테고리명", example = "건강")
        private String categoryName;

        @Schema(description = "예상 소요 시간(분)", example = "5")
        private Integer requiredMinutes;

        @Schema(description = "해당 OPU를 수행한 횟수", example = "12")
        private long completedCount;
    }
}