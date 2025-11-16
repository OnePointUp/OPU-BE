package com.opu.opube.feature.member.command.application.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateMemberProfileRequest {

    @Size(min = 2, max = 20)
    private String nickname;

    @Size(max = 100)
    private String bio;

    private String profileImageUrl;

}