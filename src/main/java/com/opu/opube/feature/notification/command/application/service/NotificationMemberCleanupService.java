package com.opu.opube.feature.notification.command.application.service;

import com.opu.opube.feature.notification.command.domain.repository.MemberNotificationSettingRepository;
import com.opu.opube.feature.notification.command.domain.repository.NotificationRepository;
import com.opu.opube.feature.notification.command.domain.repository.WebPushSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationMemberCleanupService {

    private final NotificationRepository notificationRepository;
    private final WebPushSubscriptionRepository webPushSubscriptionRepository;
    private final MemberNotificationSettingRepository memberNotificationSettingRepository;

    @Transactional
    public void deleteByMemberId(Long memberId) {
        notificationRepository.deleteByMemberId(memberId);
        memberNotificationSettingRepository.deleteByMemberId(memberId);
        webPushSubscriptionRepository.deleteByMemberId(memberId);
    }
}