package com.opu.opube.feature.member.query.service;

import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.member.command.application.dto.response.MemberProfileResponse;
import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.member.command.domain.repository.MemberRepository;
import com.opu.opube.feature.member.query.dto.response.MemberSummaryResponse;
import com.opu.opube.feature.opu.query.dto.response.OpuCountSummaryResponse;
import com.opu.opube.feature.opu.query.service.OpuQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberQueryServiceImpl implements MemberQueryService {

    private final MemberRepository memberRepository;
    private final OpuQueryService opuQueryService;

    @Override
    @Transactional(readOnly = true)
    public MemberProfileResponse getMyProfile(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        return MemberProfileResponse.from(member);
    }

    @Override
    public Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public MemberSummaryResponse getMemberSummary(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        OpuCountSummaryResponse opuSummary = opuQueryService.getOpuCountSummary(memberId);

        return MemberSummaryResponse.builder()
                .nickname(member.getNickname())
                .email(member.getEmail())
                .profileImageUrl(member.getProfileImageUrl())
                .favoriteOpuCount(opuSummary.getFavoriteOpuCount())
                .myOpuCount(opuSummary.getMyOpuCount())
                .build();
    }
}
