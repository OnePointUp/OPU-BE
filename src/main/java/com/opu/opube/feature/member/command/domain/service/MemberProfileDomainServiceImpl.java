package com.opu.opube.feature.member.command.domain.service;

import com.opu.opube.feature.auth.command.domain.service.AuthDomainService;
import com.opu.opube.feature.auth.command.domain.service.NicknameTagGenerator;
import com.opu.opube.feature.member.command.application.dto.request.UpdateMemberProfileRequest;
import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.member.command.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberProfileDomainServiceImpl implements MemberProfileDomainService {

    private final MemberRepository memberRepository;
    private final AuthDomainService authDomainService;
    private final NicknameTagGenerator nicknameTagGenerator;

    @Override
    @Transactional
    public void updateProfile(Member member, UpdateMemberProfileRequest req) {
        String newNickname = req.getNickname();

        // 닉네임 변경 로직
        if (newNickname != null && !newNickname.equals(member.getNickname())) {
            // 1) 닉네임 규칙 검증
            authDomainService.validateNickname(newNickname);

            // 2) tag 충돌 여부 확인
            String currentTag = member.getNicknameTag();
            boolean conflict = memberRepository
                    .existsByNicknameAndNicknameTag(newNickname, currentTag);

            String finalTag = conflict
                    ? nicknameTagGenerator.generate(newNickname)
                    : currentTag;

            member.updateNicknameAndTag(newNickname, finalTag);
        }

        // 프로필 내용/이미지 변경
        member.updateProfile(req.getBio(), req.getProfileImageUrl());
    }
}