package com.opu.opube.feature.auth.command.application.dto.response;

import com.opu.opube.feature.auth.command.application.dto.response.TokenResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "카카오 로그인 응답 DTO")
public class KakaoLoginResponse {

    @Schema(
            description = "추가 정보 필요 여부",
            example = "false"
    )
    private boolean needAdditionalInfo;

    @Schema(
            description = "카카오 고유 provider ID",
            example = "1234567890"
    )
    private String providerId;

    @Schema(
            description = "JWT Access/Refresh Token (가입 이력이 있을 때)",
            implementation = TokenResponse.class
    )
    private TokenResponse token;
}