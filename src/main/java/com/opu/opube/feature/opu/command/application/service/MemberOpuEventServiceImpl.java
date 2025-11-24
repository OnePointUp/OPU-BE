package com.opu.opube.feature.opu.command.application.service;

import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.opu.command.domain.aggregate.MemberOpuCounter;
import com.opu.opube.feature.opu.command.domain.aggregate.MemberOpuEvent;
import com.opu.opube.feature.opu.command.domain.aggregate.Opu;
import com.opu.opube.feature.opu.command.domain.repository.MemberOpuCounterRepository;
import com.opu.opube.feature.opu.command.domain.repository.MemberOpuEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberOpuEventServiceImpl implements MemberOpuEventService {

    private final MemberOpuEventRepository memberOpuEventRepository;

    @Override
    @Transactional
    public void completeTodo(Member member, Opu opu) {
        MemberOpuEvent memberOpuEvent = memberOpuEventRepository.findByMemberAndOpu(member, opu)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_OPU_EVENT_NOT_FOUND));

        memberOpuEvent.setCompleted();
    }
}