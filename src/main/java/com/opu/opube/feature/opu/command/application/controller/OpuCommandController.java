package com.opu.opube.feature.opu.command.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.opu.opube.feature.opu.command.application.service.OpuCommandService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/opu")
public class OpuCommandController {

    private final OpuCommandService opuCommandService;

}