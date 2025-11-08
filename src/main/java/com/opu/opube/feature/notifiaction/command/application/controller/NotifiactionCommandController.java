package com.opu.opube.feature.notifiaction.command.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.opu.opube.feature.notifiaction.command.application.service.NotifiactionCommandService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifiaction")
public class NotifiactionCommandController {

    private final NotifiactionCommandService notifiactionCommandService;

}