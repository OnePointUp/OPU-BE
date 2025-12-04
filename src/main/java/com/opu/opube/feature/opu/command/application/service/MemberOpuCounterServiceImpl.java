package com.opu.opube.feature.opu.command.application.service;

import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.opu.command.domain.aggregate.MemberOpuCounter;
import com.opu.opube.feature.opu.command.domain.aggregate.Opu;
import com.opu.opube.feature.opu.command.domain.repository.MemberOpuCounterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberOpuCounterServiceImpl implements MemberOpuCounterService {

    private final MemberOpuCounterRepository memberOpuCounterRepository;

    @Override
    @Transactional
    public void completeTodo(Member member, Opu opu) {
        MemberOpuCounter counter = memberOpuCounterRepository
                .findByMemberAndOpu(member, opu)
                .orElseGet(() -> {
                    MemberOpuCounter newCounter = MemberOpuCounter.toEntity(member, opu);
                    return memberOpuCounterRepository.save(newCounter);
                });

        counter.increaseCount();
    }

    @Override
    @Transactional
    public void cancelCompleteTodo(Member member, Opu opu) {
        memberOpuCounterRepository
                .findByMemberAndOpu(member, opu)
                .ifPresent(MemberOpuCounter::decreaseCount);
    }
}