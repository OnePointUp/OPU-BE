package com.opu.opube.feature.notification.command.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.opu.opube.feature.notification.command.application.service.NotificationCommandService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationCommandController {

    private final NotificationCommandService notificationCommandService;

}