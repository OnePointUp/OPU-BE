package com.opu.opube.feature.member.command.domain.service;

import com.opu.opube.feature.member.command.application.dto.request.UpdateMemberProfileRequest;
import com.opu.opube.feature.member.command.domain.aggregate.Member;

public interface MemberProfileDomainService {
    void updateProfile(Member member, UpdateMemberProfileRequest req);
}