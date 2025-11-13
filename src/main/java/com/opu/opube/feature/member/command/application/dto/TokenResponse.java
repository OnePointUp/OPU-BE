package com.opu.opube.feature.member.command.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;              // 보통 "Bearer"
    private long expiresInSeconds;        // access 만료 (초)
    private long refreshExpiresInSeconds; // refresh 만료 (초)
}