package com.opu.opube.feature.auth.command.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "카카오 OAuth 토큰 응답 DTO (카카오 서버에서 받은 원본 값)")
public class KakaoTokenResponse {

    @Schema(
            description = "카카오 Access Token",
            example = "vI1d3k2wxyz"
    )
    @JsonProperty("access_token")
    private String accessToken;

    @Schema(
            description = "토큰 타입",
            example = "bearer"
    )
    @JsonProperty("token_type")
    private String tokenType;

    @Schema(
            description = "카카오 Refresh Token",
            example = "Rfr3sh.k12abcd"
    )
    @JsonProperty("refresh_token")
    private String refreshToken;

    @Schema(
            description = "Access Token 만료 시간(초)",
            example = "21599"
    )
    @JsonProperty("expires_in")
    private Long expiresIn;

    @Schema(
            description = "인증 범위(scope)",
            example = "profile_nickname profile_image"
    )
    @JsonProperty("scope")
    private String scope;

    @Schema(
            description = "Refresh Token 만료 시간(초)",
            example = "5183999"
    )
    @JsonProperty("refresh_token_expires_in")
    private Long refreshTokenExpiresIn;
}