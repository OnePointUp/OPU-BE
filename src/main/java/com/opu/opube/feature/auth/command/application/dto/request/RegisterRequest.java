package com.opu.opube.feature.auth.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Schema(description = "회원가입 요청 DTO")
public class RegisterRequest {

    @Schema(
            description = "회원가입할 이메일 주소",
            example = "opu@example.com"
    )
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @Schema(
            description = "비밀번호",
            example = "password1234!"
    )
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 128, message = "비밀번호는 8자 이상 128자 이하로 입력해야 합니다.")
    private String password;

    @Schema(
            description = "닉네임",
            example = "OPU유저"
    )
    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 50, message = "닉네임은 2자 이상 50자 이하로 입력해야 합니다.")
    private String nickname;

    @Schema(
            description = "프로필 이미지 URL (선택)",
            example = "https://d111.cloudfront.net/profile/abcd.png"
    )
    private String profileImageUrl;

    @Schema(
            description = "웹 푸시 알림 동의",
            example = "true"
    )
    private Boolean webPushAgreed;
}