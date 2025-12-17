package com.opu.opube.feature.todo.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "월별 Todo 조회 응답 (캘린더 월 뷰용)")
public record TodoMonthResponse(

        @Schema(
                description = "조회 연도",
                example = "2025"
        )
        int year,

        @Schema(
                description = "조회 월 (1~12)",
                example = "12"
        )
        int month,

        @Schema(
                description = "날짜별 Todo 목록",
                implementation = TodoDayGroup.class
        )
        List<TodoDayGroup> days
) {}