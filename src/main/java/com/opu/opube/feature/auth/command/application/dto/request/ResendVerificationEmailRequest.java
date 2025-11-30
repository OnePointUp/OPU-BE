package com.opu.opube.feature.auth.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "이메일 인증 메일 재발송 요청 DTO")
public class ResendVerificationEmailRequest {
    @Schema(
            description = "인증 메일을 다시 받을 이메일 주소",
            example = "opu@example.com"
    )
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;
}