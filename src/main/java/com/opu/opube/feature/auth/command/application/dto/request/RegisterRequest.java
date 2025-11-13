package com.opu.opube.feature.auth.command.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RegisterRequest {
    @Email @NotBlank
    private String email;

    @NotBlank @Size(min = 8, max = 128)
    private String password;

    @NotBlank @Size(min = 2, max = 50)
    private String nickname;

    // 프로필 이미지 추가
}