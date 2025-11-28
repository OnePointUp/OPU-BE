package com.opu.opube.feature.auth.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "Token 재발급 요청 DTO")
public class RefreshTokenRequest {

    @Schema(
            description = "현재 보유 중인 유효한 리프레시 토큰",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
    )
    @NotBlank(message = "리프레시 토큰은 필수입니다.")
    private String refreshToken;
}