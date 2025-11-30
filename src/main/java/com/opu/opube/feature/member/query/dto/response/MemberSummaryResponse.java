package com.opu.opube.feature.member.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "회원 요약 정보 응답 DTO (마이페이지)")
public class MemberSummaryResponse {

    @Schema(
            description = "회원 닉네임",
            example = "OPU 유저"
    )
    private final String nickname;

    @Schema(
            description = "프로필 이미지 URL",
            example = "https://cdn.opu.app/users/101/profile_172000111.png",
            nullable = true
    )
    private final String profileImageUrl;

    @Schema(
            description = "회원 이메일",
            example = "opu@example.com"
    )
    private final String email;

    @Schema(
            description = "찜한 OPU 개수",
            example = "12"
    )
    private final long favoriteOpuCount;

    @Schema(
            description = "내가 만든 OPU 개수",
            example = "4"
    )
    private final long myOpuCount;

    @Schema(
            description = "프로필 소개글 (최대 100자)",
            example = "꾸준히 성장하는 백엔드 개발자입니다!"
    )
    @Size(max = 100, message = "Bio는 최대 100자까지 입력 가능합니다.")
    private final String bio;
}