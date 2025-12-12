package com.opu.opube.feature.member.query.service;

import com.opu.opube.feature.member.command.application.dto.response.MemberProfileResponse;
import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.member.query.dto.response.MemberSummaryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public interface MemberQueryService {
    MemberProfileResponse getMyProfile(Long memberId);
    Member getMember(Long memberId);
    MemberSummaryResponse getMemberSummary(Long memberId);
    boolean getWebPushAgreed(Long memberId);
}