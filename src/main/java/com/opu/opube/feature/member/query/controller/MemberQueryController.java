package com.opu.opube.feature.member.query.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.opu.opube.feature.member.query.service.MemberQueryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberQueryController {

    private final MemberQueryService memberQueryService;

}