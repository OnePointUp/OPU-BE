package com.opu.opube.feature.todo.query.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.opu.opube.feature.todo.query.service.TodoQueryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todo")
public class TodoQueryController {

    private final TodoQueryService todoQueryService;

}