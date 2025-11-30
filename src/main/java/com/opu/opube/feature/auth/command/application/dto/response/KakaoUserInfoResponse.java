package com.opu.opube.feature.auth.command.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "카카오 사용자 정보 응답 DTO (카카오 서버 원본 구조)")
public class KakaoUserInfoResponse {

    @Schema(
            description = "카카오 사용자 고유 ID",
            example = "1234567890"
    )
    private Long id;

    @Schema(
            description = "카카오 계정 정보",
            implementation = KakaoAccount.class
    )
    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    @Setter
    @Schema(description = "카카오 계정 정보 객체")
    public static class KakaoAccount {

        @Schema(
                description = "카카오 계정 이메일 (동의 필요)",
                example = "user@kakao.com"
        )
        private String email;

        @Schema(
                description = "카카오 프로필 정보",
                implementation = Profile.class
        )
        private Profile profile;
    }

    @Getter
    @Setter
    @Schema(description = "카카오 사용자 프로필 정보")
    public static class Profile {

        @Schema(
                description = "카카오 프로필 닉네임",
                example = "카카오유저"
        )
        private String nickname;
    }
}