package com.opu.opube.feature.todo.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MonthlyRoutineStats {
    private long totalCount;
    private long completedCount;
}
