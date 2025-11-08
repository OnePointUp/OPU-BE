package com.opu.opube.feature.member.command.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.opu.opube.feature.member.command.application.service.MemberCommandService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberCommandController {

    private final MemberCommandService memberCommandService;

}