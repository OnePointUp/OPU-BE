package com.opu.opube.feature.todo.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "월별 routine todo 통계 응답")
public class MonthlyRoutineTodoStatsResponse {
    @Schema(description = "routine 식별자")
    private long routineId;
    @Schema(description = "routine 제목")
    private String title;
    @Schema(description = "routine 색상")
    private String color;
    @Schema(example = "2025", description = "년도")
    private int year;
    @Schema(example = "3", description = "월")
    private int month;
    private List<DayTodoStats> days;

}