package com.opu.opube.feature.todo.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TodoStatisticsDto {
    private LocalDate date;
    private long totalCount;
    private long completedCount;
}
