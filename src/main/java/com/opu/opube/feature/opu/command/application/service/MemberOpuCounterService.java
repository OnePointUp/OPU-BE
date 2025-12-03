package com.opu.opube.feature.opu.command.application.service;

import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.opu.command.domain.aggregate.Opu;

public interface MemberOpuCounterService {
    void completeTodo(Member member, Opu opu);

    void cancelCompleteTodo(Member member, Opu opu);
    // command methods here
}