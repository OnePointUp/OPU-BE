package com.opu.opube.feature.auth.command.application.dto.request;

import lombok.Getter;

@Getter
public class ResendVerificationEmailRequest {
    private String email;
}