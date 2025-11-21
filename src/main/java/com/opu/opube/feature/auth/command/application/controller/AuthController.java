package com.opu.opube.feature.auth.command.application.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.feature.auth.command.application.dto.request.*;
import com.opu.opube.feature.auth.command.application.dto.response.KakaoLoginResponse;
import com.opu.opube.feature.auth.command.application.dto.response.RegisterResponse;
import com.opu.opube.feature.auth.command.application.dto.response.TokenResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.auth.command.application.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Value("${app.frontend-base-url}")
    private String frontendBaseUrl;   // ← 여기로 설정값 주입

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

    @GetMapping("/kakao/login")
    public ResponseEntity<ApiResponse<KakaoLoginResponse>> kakaoLogin(
            @RequestParam String code
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(authService.kakaoLogin(code))
        );
    }

    @PostMapping("/kakao/register")
    public ResponseEntity<ApiResponse<TokenResponse>> kakaoRegister(
            @RequestBody @Valid KakaoRegisterRequest req
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(authService.kakaoRegister(req))
        );
    }

    @PostMapping("/password/reset-request")
    public ResponseEntity<ApiResponse<Void>> requestPasswordReset(
            @RequestBody PasswordResetRequest req
    ) {
        authService.requestPasswordReset(req, frontendBaseUrl);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestBody PasswordResetConfirmRequest req
    ) {
        authService.resetPassword(req);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/verify/resend")
    public ResponseEntity<ApiResponse<Void>> resendVerificationEmail(
            @RequestBody ResendVerificationEmailRequest req
    ) {
        authService.resendVerificationEmail(req.getEmail(), backendBaseUrl);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/password/change")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestBody @Valid ChangePasswordRequest req,
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        Long memberId = principal.getMemberId();
        authService.changePassword(memberId, req);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}