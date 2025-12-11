package com.opu.opube.feature.todo.command.application.listener;

import com.opu.opube.feature.member.command.domain.event.MemberDeactivatedEvent;
import com.opu.opube.feature.todo.command.application.service.TodoMemberCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class TodoMemberDeactivatedListener {

    private final TodoMemberCleanupService todoMemberCleanupService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(MemberDeactivatedEvent event) {
        Long memberId = event.memberId();
        log.info("[TODO] MemberDeactivatedEvent received. memberId={}", memberId);

        todoMemberCleanupService.deleteByMemberId(memberId);
    }
}