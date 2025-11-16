package com.opu.opube.feature.member.command.application.dto.response;

import com.opu.opube.feature.member.command.domain.aggregate.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberProfileResponse {

    private Long id;
    private String email;
    private String nickname;
    private String bio;
    private String profileImageUrl;

    public static MemberProfileResponse from(Member member) {
        return MemberProfileResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .bio(member.getBio())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }
}