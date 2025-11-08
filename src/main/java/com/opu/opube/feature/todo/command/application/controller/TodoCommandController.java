package com.opu.opube.feature.todo.command.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.opu.opube.feature.todo.command.application.service.TodoCommandService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todo")
public class TodoCommandController {

    private final TodoCommandService todoCommandService;

}