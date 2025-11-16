package com.opu.opube.feature.member.command.application.service;

import com.opu.opube.feature.member.command.application.dto.request.UpdateMemberProfileRequest;
import com.opu.opube.feature.member.command.application.dto.response.MemberProfileResponse;

public interface MemberCommandService {
    MemberProfileResponse updateProfile(Long memberId, UpdateMemberProfileRequest req);

}