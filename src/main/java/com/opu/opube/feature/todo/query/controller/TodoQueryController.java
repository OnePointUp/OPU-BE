package com.opu.opube.feature.todo.query.controller;

import com.opu.opube.common.dto.PageResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.todo.query.dto.response.TodoResponseDto;
import com.opu.opube.feature.todo.query.dto.response.TodoStatisticsDto;
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
@RequestMapping("/api/v1/todos")
public class TodoQueryController {

    private final TodoQueryService todoQueryService;

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

    /**
     * 주 단위 Todo 통계 조회
     * @param startDate 조회 시작일 (일요일)
     */
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