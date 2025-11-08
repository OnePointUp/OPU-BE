package com.opu.opube.feature.notifiaction.query.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.opu.opube.feature.notifiaction.query.service.NotifiactionQueryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifiaction")
public class NotifiactionQueryController {

    private final NotifiactionQueryService notifiactionQueryService;

}