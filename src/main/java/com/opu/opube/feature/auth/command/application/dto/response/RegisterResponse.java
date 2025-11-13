package com.opu.opube.feature.auth.command.application.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RegisterResponse {
    private Long memberId;
    private String message;
}