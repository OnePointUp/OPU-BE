package com.opu.opube.feature.todo.query.controller;

import com.opu.opube.common.dto.PageResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.todo.query.dto.response.TodoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.opu.opube.feature.todo.query.service.TodoQueryService;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/debug/todo")
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

}