package com.opu.opube.feature.opu.command.application.listener;

import com.opu.opube.feature.member.command.domain.event.MemberDeactivatedEvent;
import com.opu.opube.feature.opu.command.application.service.OpuMemberCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class OpuMemberDeactivatedListener {

    private final OpuMemberCleanupService opuMemberCleanupService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(MemberDeactivatedEvent event) {
        Long memberId = event.getMemberId();
        log.info("[OPU] MemberDeactivatedEvent received. memberId={}", memberId);

        opuMemberCleanupService.deleteByMemberId(memberId);
    }
}