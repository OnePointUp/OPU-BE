package com.opu.opube.feature.member.command.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private Long expiresInSeconds;
}