package com.opu.opube.feature.todo.query.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.common.dto.PageResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.todo.query.dto.response.MonthlyRoutineTodoStatsResponse;
import com.opu.opube.feature.todo.query.dto.response.RoutineDetailResponseDto;
import com.opu.opube.feature.todo.query.dto.response.RoutineListResponseDto;
import com.opu.opube.feature.todo.query.dto.response.RoutineSummaryResponseDto;
import com.opu.opube.feature.todo.query.service.RoutineQueryService;
import com.opu.opube.feature.todo.query.service.TodoQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/routines")
@Tag(name = "Routine Query API", description = "routine 조회 API")
public class RoutineQueryController {

    private final RoutineQueryService routineQueryService;
    private final TodoQueryService todoQueryService;

    @Operation(
            summary = "routine 목록 조회",
            description = "사용자가 등록한 routine을 조회합니다."
    )
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

    @Operation(
            summary = "routine 상세 조회",
            description = "id로 사용자가 등록한 routine의 상세를 조회합니다."
    )
    @GetMapping("/{routineId}")
    public ResponseEntity<ApiResponse<RoutineDetailResponseDto>> getRoutineById(
            @AuthenticationPrincipal MemberPrincipal memberPrincipal,
            @PathVariable Long routineId
    ) {
        Long memberId = memberPrincipal.getMemberId();
        RoutineDetailResponseDto routine = routineQueryService.getRoutine(memberId, routineId);
        return ResponseEntity.ok(ApiResponse.success(routine));
    }

    @Operation(
            summary = "routine 목록 조회 (통계)",
            description = "routine의 제목, id를 반환합니다."
    )
    @GetMapping("/summary")
    public ResponseEntity<PageResponse<RoutineSummaryResponseDto>> getRoutineTitleList(
            @AuthenticationPrincipal MemberPrincipal memberPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long memberId = memberPrincipal.getMemberId();
        PageResponse<RoutineSummaryResponseDto> summaries = routineQueryService.getRoutineTitleList(memberId, page, size);
        return ResponseEntity.ok(summaries);
    }

    @Operation(
            summary = "routine의 월별 todo 목록 조회 (통계)",
            description = "월의 각 날짜에 대해 이 routine 으로 생성된 todo가 있었는지 & 있었으면 수행 되었는지를 확인할 수 있습니다."
    )
    @GetMapping("/{routineId}/todos/stats")
    public ResponseEntity<ApiResponse<MonthlyRoutineTodoStatsResponse >> getRoutineTodoStats(
            @AuthenticationPrincipal MemberPrincipal memberPrincipal,
            @PathVariable Long routineId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        Long memberId = memberPrincipal.getMemberId();
        String title = routineQueryService.getRoutine(memberId, routineId).getTitle();
        MonthlyRoutineTodoStatsResponse summaries = todoQueryService.getRoutineStat(memberId, routineId, title, year, month);
        return ResponseEntity.ok(ApiResponse.success(summaries));
    }
}