package com.opu.opube.feature.auth.command.application.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.feature.auth.command.application.dto.request.LoginRequest;
import com.opu.opube.feature.auth.command.application.dto.request.RefreshTokenRequest;
import com.opu.opube.feature.auth.command.application.dto.request.RegisterRequest;
import com.opu.opube.feature.auth.command.application.dto.response.RegisterResponse;
import com.opu.opube.feature.auth.command.application.dto.response.TokenResponse;
import com.opu.opube.feature.auth.command.application.service.AuthService;
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

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody @Valid LoginRequest req) {
        TokenResponse tokens = authService.login(req.getEmail(), req.getPassword());
        return ResponseEntity.ok(ApiResponse.success(tokens));
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verify(@RequestParam("token") String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.success("이메일 인증이 완료되었습니다."));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@RequestBody @Valid RefreshTokenRequest req) {
        TokenResponse tokenResponse = authService.refreshToken(req);
        return ResponseEntity.ok(ApiResponse.success(tokenResponse));
    }
}