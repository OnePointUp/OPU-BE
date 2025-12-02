package com.opu.opube.feature.todo.query.infrastructure.repository;

import com.opu.opube.common.dto.PageResponse;
import com.opu.opube.feature.todo.query.dto.response.RoutineListResponseDto;

public interface RoutineQueryRepository {
    PageResponse<RoutineListResponseDto> getRoutines(Long memberId, int page, int size);
}