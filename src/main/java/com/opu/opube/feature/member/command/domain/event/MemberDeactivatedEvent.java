package com.opu.opube.feature.member.command.domain.event;

import lombok.Getter;

@Getter
public class MemberDeactivatedEvent {

    private final Long memberId;

    public MemberDeactivatedEvent(Long memberId) {
        this.memberId = memberId;
    }
}