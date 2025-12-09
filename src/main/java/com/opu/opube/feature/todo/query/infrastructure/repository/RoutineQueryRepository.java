package com.opu.opube.feature.todo.query.infrastructure.repository;

import com.opu.opube.common.dto.PageResponse;
import com.opu.opube.feature.todo.query.dto.response.RoutineDetailResponseDto;
import com.opu.opube.feature.todo.query.dto.response.RoutineListResponseDto;
import com.opu.opube.feature.todo.query.dto.response.RoutineStatResponseDto;
import com.opu.opube.feature.todo.query.dto.response.RoutineSummaryResponseDto;

public interface RoutineQueryRepository {
    PageResponse<RoutineListResponseDto> getRoutines(Long memberId, int page, int size);

    RoutineDetailResponseDto getRoutine(Long memberId, Long routineId);

    PageResponse<RoutineSummaryResponseDto> getRoutineTitle(Long memberId, int page, int size);

    PageResponse<RoutineStatResponseDto> getRoutineStatList(Long memberId, int page, int size);
}