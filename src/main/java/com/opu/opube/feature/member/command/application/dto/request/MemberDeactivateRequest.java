package com.opu.opube.feature.member.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class MemberDeactivateRequest {

    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String currentPassword;
}