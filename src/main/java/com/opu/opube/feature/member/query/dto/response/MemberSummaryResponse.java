package com.opu.opube.feature.member.query.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberSummaryResponse {
    private final String nickname;
    private final String profileImageUrl;
    private final String email;
    private final long favoriteOpuCount;
    private final long myOpuCount;
}