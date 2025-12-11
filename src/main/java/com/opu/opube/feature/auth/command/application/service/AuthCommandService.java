package com.opu.opube.feature.auth.command.application.service;

import com.opu.opube.feature.auth.command.application.dto.request.*;
import com.opu.opube.feature.auth.command.application.dto.response.KakaoLoginResponse;
import com.opu.opube.feature.auth.command.application.dto.response.TokenResponse;

public interface AuthCommandService {

    Long register(RegisterRequest req, String backendBaseUrl);

    TokenResponse login(String email, String rawPassword);

    TokenResponse refreshToken(RefreshTokenRequest req);

    void verifyEmail(String token);

    void requestPasswordReset(PasswordResetRequest req, String frontendBaseUrl);

    void resetPassword(PasswordResetConfirmRequest req);

    KakaoLoginResponse kakaoLogin(String code);

    TokenResponse kakaoRegister(KakaoRegisterRequest req);

    void resendVerificationEmail(String email, String backendBaseUrl);

    void changePassword(Long memberId, ChangePasswordRequest req);

    void checkCurrentPassword(Long memberId, String rawPassword);

    void logout(Long memberId);

    boolean isEmailVerified(String email);

    void unlinkSocialIfNeeded(com.opu.opube.feature.member.command.domain.aggregate.Member member);
}

