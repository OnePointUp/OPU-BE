package com.opu.opube.feature.member.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "회원 프로필 수정 요청 DTO")
public class UpdateMemberProfileRequest {

    @Schema(
            description = "변경할 닉네임",
            example = "OPU 유저"
    )
    @Size(min = 2, max = 20, message = "닉네임은 2~20자여야 합니다.")
    private String nickname;

    @Schema(
            description = "프로필 소개글 (최대 100자)",
            example = "꾸준히 성장하는 백엔드 개발자입니다!"
    )
    @Size(max = 100, message = "Bio는 최대 100자까지 입력 가능합니다.")
    private String bio;


    @Schema(
            description = "프로필 이미지 URL (S3 Presigned URL로 업로드 후 받은 경로)",
            example = "https://cdn.opu.app/users/101/profile_171000123.png"
    )
    private String profileImageUrl;

}