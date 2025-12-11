package com.opu.opube.feature.notification.command.application.listener;

import com.opu.opube.feature.member.command.domain.event.MemberDeactivatedEvent;
import com.opu.opube.feature.notification.command.application.service.NotificationMemberCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationMemberDeactivatedListener {

    private final NotificationMemberCleanupService notificationMemberCleanupService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(MemberDeactivatedEvent event) {
        Long memberId = event.memberId();
        log.info("[NOTIFICATION] MemberDeactivatedEvent received. memberId={}", memberId);

        notificationMemberCleanupService.deleteByMemberId(memberId);
    }
}