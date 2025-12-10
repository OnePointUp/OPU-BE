package com.opu.opube.feature.todo.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoStatRow {
    @Schema(example = "3")
    private long routineId;

    @Schema(example = "2025-03-01")
    private LocalDate date;

    /**
     * 해당 날짜에 routine 으로 생성된 todos 존재 여부
     */
    @Schema(description = "해당 날짜에 routine todo 존재 여부")
    private boolean hasTodo;

    /**
     * hasTodo == true 일 때만 의미 있음
     * - true  : 생성 + 완료
     * - false : 생성 + 미완료
     * - null  : todos 없음
     */
    @Schema(
            description = "todo 수행 여부 (todo 없을 경우 null)",
            nullable = true
    )
    private Boolean done;
}
