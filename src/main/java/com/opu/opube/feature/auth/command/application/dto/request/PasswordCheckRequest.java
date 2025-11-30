package com.opu.opube.feature.auth.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordCheckRequest {

    @Schema(
            description = "현재 사용 중인 비밀번호",
            example = "currentPassword1234"
    )
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}