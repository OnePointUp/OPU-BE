package com.opu.opube.feature.auth.command.application.dto.response;

import com.opu.opube.feature.auth.command.application.dto.response.TokenResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoLoginResponse {
    private boolean needAdditionalInfo;   // true → 추가 정보 필요
    private String providerId;            // 카카오 고유 ID
    private TokenResponse token;          // 기존회원일 때만 발급
}