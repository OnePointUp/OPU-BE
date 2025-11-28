package com.opu.opube.feature.auth.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "JWT 토큰 응답 DTO")
public class TokenResponse {

    @Schema(
            description = "Access Token",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    private String accessToken;

    @Schema(
            description = "Refresh Token",
            example = "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9..."
    )
    private String refreshToken;

    @Schema(
            description = "토큰 타입",
            example = "Bearer",
            defaultValue = "Bearer"
    )
    private String tokenType;

    @Schema(
            description = "Access Token 만료 시간",
            example = "3600"
    )
    private long expiresInSeconds;

    @Schema(
            description = "Refresh Token 만료 시간",
            example = "1209600"
    )
    private long refreshExpiresInSeconds;
}