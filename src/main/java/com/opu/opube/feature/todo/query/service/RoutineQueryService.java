package com.opu.opube.feature.todo.query.service;

import com.opu.opube.common.dto.PageResponse;
import com.opu.opube.feature.todo.query.dto.response.RoutineDetailResponseDto;
import com.opu.opube.feature.todo.query.dto.response.RoutineListResponseDto;
import com.opu.opube.feature.todo.query.dto.response.RoutineSummaryResponseDto;
import com.opu.opube.feature.todo.query.infrastructure.repository.RoutineQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoutineQueryService {

    private final RoutineQueryRepository routineQueryRepository;

    public PageResponse<RoutineListResponseDto> getRoutineList(Long memberId, int page, int size) {
        return routineQueryRepository.getRoutines(memberId, page, size);
    }

    public RoutineDetailResponseDto getRoutine(Long memberId, Long routineId) {
        return routineQueryRepository.getRoutine(memberId, routineId);
    }

    public PageResponse<RoutineSummaryResponseDto> getRoutineTitleList(Long memberId, int page, int size) {
        return routineQueryRepository.getRoutineTitle(memberId, page, size);
    }
}