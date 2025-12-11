package com.opu.opube.feature.auth.command.domain.service;

import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.member.command.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class NicknameTagGenerator {

    private static final int MAX_RETRY_COUNT = 5;
    private static final int TAG_MIN = 1000;
    private static final int TAG_MAX = 10000;

    private final MemberRepository memberRepository;

    public String generate(String nickname) {
        for (int i = 0; i < MAX_RETRY_COUNT; i++) {
            int num = ThreadLocalRandom.current().nextInt(TAG_MIN, TAG_MAX);
            String tag = String.valueOf(num);
            
            if (!memberRepository.existsByNicknameAndNicknameTag(nickname, tag)) {
                return tag;
            }
        }
        
        throw new BusinessException(
                ErrorCode.INTERNAL_SERVER_ERROR,
                "닉네임 태그 생성에 실패했습니다."
        );
    }
}

