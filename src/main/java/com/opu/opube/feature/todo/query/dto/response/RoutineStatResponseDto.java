package com.opu.opube.feature.todo.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutineStatResponseDto {
    String title;
    long id;
    String color;
}
