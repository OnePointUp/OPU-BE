package com.opu.opube.feature.auth.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "로그인 요청 DTO")
public class LoginRequest {

    @Schema(
            description = "로그인할 이메일 주소",
            example = "opu@example.com"
    )
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    @NotBlank
    private String email;

    @Schema(
            description = "비밀번호",
            example = "password1234"
    )
    @NotBlank
    private String password;
}