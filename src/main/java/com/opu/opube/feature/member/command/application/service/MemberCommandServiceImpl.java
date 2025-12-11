package com.opu.opube.feature.member.command.application.service;

import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.member.command.application.dto.request.UpdateMemberProfileRequest;
import com.opu.opube.feature.member.command.application.dto.response.MemberProfileResponse;
import com.opu.opube.feature.auth.command.domain.service.AuthDomainService;
import com.opu.opube.feature.auth.command.domain.service.NicknameTagGenerator;
import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.member.command.domain.repository.MemberRepository;
import com.opu.opube.feature.notification.command.application.service.NotificationMemberCleanupService;
import com.opu.opube.feature.opu.command.application.service.OpuMemberCleanupService;
import com.opu.opube.feature.todo.command.application.service.TodoMemberCleanupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberCommandServiceImpl implements MemberCommandService {

    private final MemberRepository memberRepository;
    private final TodoMemberCleanupService todoMemberCleanupService;
    private final NotificationMemberCleanupService notificationMemberCleanupService;
    private final OpuMemberCleanupService opuMemberCleanupService;
    private final AuthDomainService authDomainService;
    private final NicknameTagGenerator nicknameTagGenerator;

    @Override
    @Transactional
    public MemberProfileResponse updateProfile(Long memberId, UpdateMemberProfileRequest req) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        String newNickname = req.getNickname();
        String newBio = req.getBio();
        String newProfileImageUrl = req.getProfileImageUrl();

        if (newNickname != null && !newNickname.equals(member.getNickname())) {
            authDomainService.validateNickname(newNickname);

            String currentTag = member.getNicknameTag();

            boolean conflict = memberRepository
                    .existsByNicknameAndNicknameTag(
                            newNickname, currentTag
                    );

            String finalTag = conflict
                    ? nicknameTagGenerator.generate(newNickname)
                    : currentTag;

            member.updateNicknameAndTag(newNickname, finalTag);
        }

        member.updateProfile(newBio, newProfileImageUrl);

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
    public void deactivateMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (member.isDeleted()) {
            return; // 이미 탈퇴된 경우 멱등 처리
        }

        member.deactivate();

        todoMemberCleanupService.deleteByMemberId(memberId);
        notificationMemberCleanupService.deleteByMemberId(memberId);
        opuMemberCleanupService.deleteByMemberId(memberId);
    }
}