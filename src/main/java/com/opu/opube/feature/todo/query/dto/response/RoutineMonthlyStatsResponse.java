package com.opu.opube.feature.todo.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "루틴 월간 통계 응답 DTO")
public class RoutineMonthlyStatsResponse {

    @Schema(description = "루틴 ID", example = "3")
    private Long routineId;

    @Schema(description = "연도", example = "2025")
    private int year;

    @Schema(description = "월", example = "12")
    private int month;

    @Schema(
            description = "월별 달성률 — 그 달의 예정된 날짜 중 완료한 날짜 비율",
            example = "67"
    )
    private int achievementRate;

    @Schema(
            description = """
                    연속 성공 일수(스트릭)
                    - 루틴이 예정된 날 기준으로 연속 완료된 일수
                    """,
            example = "5"
    )
    private int streakDays;

    @Schema(
            description = "이번 달 완료된 날짜 수(해당 월의 루틴 todo 중 완료한 날짜 수)",
            example = "10"
    )
    private long completedCount;

    @Schema(
            description = "이번 달 루틴이 예정된 날짜 수(해당 월에 생성된 루틴 todo 날짜 수)",
            example = "15"
    )
    private long scheduledCount;
}