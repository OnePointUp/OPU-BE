package com.opu.opube.feature.auth.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "카카오 회원가입 요청 DTO")
public class KakaoRegisterRequest {

    @Schema(
            description = "카카오 제공 provider ID",
            example = "1234567890"
    )
    @NotBlank
    private String providerId;

    @Schema(
            description = "닉네임",
            example = "opu_user"
    )
    @NotBlank
    private String nickname;

    @Schema(
            description = "프로필 이미지 URL (선택)",
            example = "https://d1234.cloudfront.net/profile/sample.png"
    )
    private String profileImageUrl;
}