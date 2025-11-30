package com.opu.opube.feature.auth.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "비밀번호 재설정 요청 DTO")
public class PasswordResetConfirmRequest {

    @Schema(
            description = "이메일로 전달된 비밀번호 재설정 토큰",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
    )
    @NotBlank(message = "재설정 토큰은 필수입니다.")
    private String token;

    @Schema(
            description = "새 비밀번호",
            example = "newPassword1234"
    )
    @NotBlank(message = "새 비밀번호는 필수입니다.")
    private String newPassword;
}