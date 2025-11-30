package com.opu.opube.feature.auth.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "비밀번호 변경 요청 DTO")
public class ChangePasswordRequest {

    @Schema(
            description = "현재 비밀번호",
            example = "oldPassword1234"
    )
    @NotBlank(message = "기존 비밀번호는 필수입니다.")
    private String oldPassword;

    @Schema(
            description = "새 비밀번호",
            example = "newPassword1234"
    )
    @NotBlank(message = "새 비밀번호는 필수입니다.")
    private String newPassword;
}