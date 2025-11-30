package com.opu.opube.feature.auth.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "회원가입 성공 응답 DTO")
public class RegisterResponse {

    @Schema(
            description = "생성된 회원 ID",
            example = "101"
    )
    private Long memberId;

    @Schema(
            description = "회원가입 성공 메시지",
            example = "회원가입 성공. 이메일을 확인하세요."
    )
    private String message;
}