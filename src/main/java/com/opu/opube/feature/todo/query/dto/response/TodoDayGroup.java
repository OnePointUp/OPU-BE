package com.opu.opube.feature.todo.query.dto.response;

import java.time.LocalDate;
import java.util.List;

public record TodoDayGroup(
        LocalDate date,
        List<TodoResponseDto> todos
) {}