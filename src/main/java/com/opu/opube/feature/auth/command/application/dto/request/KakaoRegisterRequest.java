package com.opu.opube.feature.auth.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoRegisterRequest {
    @NotBlank
    private String providerId;

    @NotBlank
    private String nickname;

    //추후 알림 설정 추가
}