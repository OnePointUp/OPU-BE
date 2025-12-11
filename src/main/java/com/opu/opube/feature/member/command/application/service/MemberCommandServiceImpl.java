package com.opu.opube.feature.member.command.application.service;

import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.auth.command.application.service.AuthCommandService;
import com.opu.opube.feature.member.command.application.dto.request.UpdateMemberProfileRequest;
import com.opu.opube.feature.member.command.application.dto.response.MemberProfileResponse;
import com.opu.opube.feature.auth.command.domain.service.AuthDomainService;
import com.opu.opube.feature.auth.command.domain.service.NicknameTagGenerator;
import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.member.command.domain.event.MemberDeactivatedEvent;
import com.opu.opube.feature.member.command.domain.repository.MemberRepository;
import com.opu.opube.feature.member.command.domain.service.MemberProfileDomainService;
import com.opu.opube.feature.notification.command.application.service.NotificationMemberCleanupService;
import com.opu.opube.feature.opu.command.application.service.OpuMemberCleanupService;
import com.opu.opube.feature.todo.command.application.service.TodoMemberCleanupService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberCommandServiceImpl implements MemberCommandService {

    private final MemberRepository memberRepository;
    private final MemberProfileDomainService memberProfileDomainService;
    private final AuthCommandService authCommandService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public MemberProfileResponse updateProfile(Long memberId, UpdateMemberProfileRequest req) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        memberProfileDomainService.updateProfile(member, req);

        return MemberProfileResponse.from(member);
    }

    @Override
    @Transactional
    public void updateWebPushAgreement(Long memberId, Boolean agreed) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        member.updateWebPushAgreed(Boolean.TRUE.equals(agreed));
    }

    @Override
    @Transactional
    public void deactivateMember(Long memberId, String currentPasswordOrNull) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (member.isDeleted()) {
            return; // 이미 탈퇴된 경우 멱등 처리
        }

        // 1) local 계정이면 비밀번호 필수 + 검증
        if (member.isLocalAccount()) {
            if (currentPasswordOrNull == null || currentPasswordOrNull.isBlank()) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR);
            }
            authCommandService.checkCurrentPassword(memberId, currentPasswordOrNull);
        }

        // 2) 소셜 계정 unlink
        authCommandService.unlinkSocialIfNeeded(member);

        // 3) soft delete (개인정보 제거)
        member.deactivate();

        // 4) 이벤트 발행 (트랜잭션 안에서)
        eventPublisher.publishEvent(new MemberDeactivatedEvent(memberId));

        // 5) Refresh Token 제거 (로그아웃)
        authCommandService.logout(memberId);
    }
}