package com.opu.opube.feature.member.command.application.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RegisterResponse {
    private Long memberId;
    private String message;
}