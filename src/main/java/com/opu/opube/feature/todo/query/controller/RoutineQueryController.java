package com.opu.opube.feature.todo.query.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.common.dto.PageResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.todo.query.dto.response.RoutineDetailResponseDto;
import com.opu.opube.feature.todo.query.dto.response.RoutineListResponseDto;
import com.opu.opube.feature.todo.query.service.RoutineQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/routine")
public class RoutineQueryController {

    private final RoutineQueryService routineQueryService;

    @GetMapping
    public ResponseEntity<PageResponse<RoutineListResponseDto>> getRoutines(
            @AuthenticationPrincipal MemberPrincipal memberPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long memberId = memberPrincipal.getMemberId();
        PageResponse<RoutineListResponseDto> routines = routineQueryService.getRoutineList(memberId, page, size);
        return ResponseEntity.ok(routines);
    }

    @GetMapping("/{routineId}")
    public ResponseEntity<ApiResponse<RoutineDetailResponseDto>> getRoutineById(
            @AuthenticationPrincipal MemberPrincipal memberPrincipal,
            @PathVariable Long routineId
    ) {
        Long memberId = memberPrincipal.getMemberId();
        RoutineDetailResponseDto routine = routineQueryService.getRoutine(memberId, routineId);
        return ResponseEntity.ok(ApiResponse.success(routine));
    }
}