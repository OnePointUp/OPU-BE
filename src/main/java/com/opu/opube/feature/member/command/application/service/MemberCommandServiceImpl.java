package com.opu.opube.feature.member.command.application.service;

import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.member.command.application.dto.request.UpdateMemberProfileRequest;
import com.opu.opube.feature.member.command.application.dto.response.MemberProfileResponse;
import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.member.command.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class MemberCommandServiceImpl implements MemberCommandService {

    private static final int NICKNAME_MIN_LENGTH = 2;
    private static final int NICKNAME_MAX_LENGTH = 20;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public MemberProfileResponse updateProfile(Long memberId, UpdateMemberProfileRequest req) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        String newNickname = req.getNickname();
        String newBio = req.getBio();
        String newProfileImageUrl = req.getProfileImageUrl();

        if (newNickname != null && !newNickname.equals(member.getNickname())) {
            validateNickname(newNickname);

            String currentTag = member.getNicknameTag();

            boolean conflict = memberRepository
                    .existsByNicknameAndNicknameTag(
                            newNickname, currentTag
                    );

            String finalTag = currentTag;

            if (conflict) {
                finalTag = generateNicknameTag(newNickname); // 회원가입에서 쓰던 메서드 재사용
            }

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


    private void validateNickname(String nickname) {
        if (nickname == null ||
                nickname.length() < NICKNAME_MIN_LENGTH ||
                nickname.length() > NICKNAME_MAX_LENGTH) {
            throw new BusinessException(ErrorCode.INVALID_NICKNAME_LENGTH);
        }
    }

    private String generateNicknameTag(String nickname) {
        for (int i = 0; i < 5; i++) {
            int num = ThreadLocalRandom.current().nextInt(1000, 10000); // 1000~9999
            String tag = String.valueOf(num);
            boolean exists = memberRepository.existsByNicknameAndNicknameTag(nickname, tag);
            if (!exists) {
                return tag;
            }
        }
        throw new BusinessException(
                ErrorCode.INTERNAL_SERVER_ERROR,
                "닉네임 태그 생성에 실패했습니다."
        );
    }
}