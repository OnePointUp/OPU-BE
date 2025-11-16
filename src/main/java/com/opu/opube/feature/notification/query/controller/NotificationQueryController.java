package com.opu.opube.feature.notification.query.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.opu.opube.feature.notification.query.service.NotificationQueryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationQueryController {

    private final NotificationQueryService notificationQueryService;

}