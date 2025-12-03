package com.opu.opube.feature.opu.command.application.service;

import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.opu.command.domain.aggregate.MemberOpuEvent;
import com.opu.opube.feature.opu.command.domain.aggregate.Opu;
import com.opu.opube.feature.opu.command.domain.repository.MemberOpuEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberOpuEventServiceImpl implements MemberOpuEventService {

    private final MemberOpuEventRepository memberOpuEventRepository;

    @Override
    @Transactional
    public void completeEvent(Member member, Opu opu) {
        MemberOpuEvent event = MemberOpuEvent.toEntity(
                member,
                opu,
                LocalDateTime.now()
        );

        memberOpuEventRepository.save(event);
    }

    @Override
    @Transactional
    public void cancelEvent(Member member, Opu opu) {
        // 가장 최근 수행 이벤트 하나만 삭제
        memberOpuEventRepository.findTopByMemberAndOpuOrderByCompletedAtDesc(member, opu)
                .ifPresent(memberOpuEventRepository::delete);
    }
}