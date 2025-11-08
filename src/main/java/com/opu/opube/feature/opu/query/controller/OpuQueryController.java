package com.opu.opube.feature.opu.query.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.opu.opube.feature.opu.query.service.OpuQueryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/opu")
public class OpuQueryController {

    private final OpuQueryService opuQueryService;

}