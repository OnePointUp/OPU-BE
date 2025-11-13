package com.opu.opube.feature.member.command.application.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.feature.member.command.application.dto.RegisterRequest;
import com.opu.opube.feature.member.command.application.dto.RegisterResponse;
import com.opu.opube.feature.member.command.application.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    String backendBaseUrl = "http://localhost:8080";

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @RequestBody @Valid RegisterRequest req) {

        Long id = authService.register(req, backendBaseUrl);

        RegisterResponse response = RegisterResponse.builder()
                .memberId(id)
                .message("회원가입 성공. 이메일을 확인하세요.")
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verify(@RequestParam("token") String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.success("이메일 인증이 완료되었습니다."));
    }
}