package com.opu.opube.feature.todo.query.controller;

import com.opu.opube.common.dto.PageResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.todo.query.dto.response.TodoMonthResponse;
import com.opu.opube.feature.todo.query.dto.response.TodoResponseDto;
import com.opu.opube.feature.todo.query.dto.response.TodoStatisticsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.opu.opube.feature.todo.query.service.TodoQueryService;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Todo Query API", description = "todo 조회 API")
@RequestMapping("/api/v1/todos")
public class TodoQueryController {

    private final TodoQueryService todoQueryService;

    @Operation(
            summary = "일일 todo 조회",
            description = "사용자가 오늘 해야할 todo를 조회합니다."
    )
    @GetMapping
    public ResponseEntity<PageResponse<TodoResponseDto>> getTodoByDate(
            @AuthenticationPrincipal MemberPrincipal memberPrincipal,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long memberId = memberPrincipal.getMemberId();
        PageResponse<TodoResponseDto> todos = todoQueryService.getTodoByUserAndDate(memberId, date, page, size);
        return ResponseEntity.ok(todos);
    }

    @Operation(
            summary = "월별 todo 조회",
            description = "캘린더 월 뷰에서 표시하기 위해 해당 월의 todo를 날짜별로 묶어 조회합니다."
    )
    @GetMapping("/month")
    public ResponseEntity<TodoMonthResponse> getTodosInMonth(
            @AuthenticationPrincipal MemberPrincipal memberPrincipal,
            @RequestParam int year,
            @RequestParam int month
    ) {
        Long memberId = memberPrincipal.getMemberId();
        return ResponseEntity.ok(todoQueryService.getTodosInMonth(memberId, year, month));
    }

    /**
     * 주 단위 Todo 통계 조회
     * @param startDate 조회 시작일 (일요일)
     */
    @Operation(
            summary = "주간 todo 조회",
            description = "하루에 총 todo, 그 중 완료한 todo를 일주일 단위로 조회합니다."
    )
    @GetMapping("/weekly")
    public ResponseEntity<List<TodoStatisticsDto>> getWeeklyStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal
    ) {
        Long memberId = memberPrincipal.getMemberId();
        // 시작일 기준으로 7일 범위 계산
        LocalDate endDate = startDate.plusDays(6);
        List<TodoStatisticsDto> statistics = todoQueryService.getStatisticsByDateRange(memberId, startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    /**
     * 월 단위 Todo 통계 조회
     * @param year 조회 연도
     * @param month 조회 월 (1~12)
     */
    @Operation(
            summary = "월간 todo 조회",
            description = "하루에 총 todo, 그 중 완료한 todo를 월 단위로 조회합니다."
    )
    @GetMapping("/monthly")
    public ResponseEntity<List<TodoStatisticsDto>> getMonthlyStatistics(
            @RequestParam int year,
            @RequestParam int month,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal
    ) {
        Long memberId = memberPrincipal.getMemberId();
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        List<TodoStatisticsDto> statistics = todoQueryService.getStatisticsByDateRange(memberId, startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

}