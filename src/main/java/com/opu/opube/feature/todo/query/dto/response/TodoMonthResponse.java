package com.opu.opube.feature.todo.query.dto.response;

import java.util.List;

public record TodoMonthResponse(
        int year,
        int month,
        List<TodoDayGroup> days
) {}

