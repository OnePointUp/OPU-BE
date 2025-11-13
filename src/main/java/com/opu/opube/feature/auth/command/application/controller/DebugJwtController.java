package com.opu.opube.feature.auth.command.application.controller;

import com.opu.opube.common.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/debug")
@RequiredArgsConstructor
public class DebugJwtController {

    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/validate-token")
    public String validate(@RequestParam String token) {
        jwtTokenProvider.validateToken(token); // 유효하면 예외 없음
        return "VALID";
    }
}