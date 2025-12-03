package com.opu.opube.feature.member.command.application.dto.response;

import com.opu.opube.feature.member.command.domain.aggregate.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "회원 프로필 응답 DTO")
public class MemberProfileResponse {

    @Schema(
            description = "회원 고유 ID",
            example = "101"
    )
    private Long id;

    @Schema(
            description = "회원 이메일 주소",
            example = "opu@example.com"
    )
    private String email;

    @Schema(
            description = "회원 닉네임",
            example = "OPU 유저"
    )
    private String nickname;

    @Schema(
            description = "닉네임 태그",
            example = "1234"
    )
    private String nicknameTag;

    @Schema(
            description = "회원 소개글 (Bio)",
            example = "꾸준히 성장하는 백엔드 개발자입니다.",
            nullable = true
    )
    private String bio;

    @Schema(
            description = "프로필 이미지 URL",
            example = "https://cdn.opu.app/users/101/profile_17100123.png"
    )
    private String profileImageUrl;

    @Schema(
            description = "auth provider",
            example = "local"
    )
    private String authProvider;

    public static MemberProfileResponse from(Member member) {
        return MemberProfileResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .nicknameTag(member.getNicknameTag())
                .bio(member.getBio())
                .profileImageUrl(member.getProfileImageUrl())
                .authProvider(member.getAuthProvider())
                .build();
    }
}