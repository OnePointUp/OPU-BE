package com.opu.opube.feature.notification.command.application.service;

import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.notification.command.application.dto.response.NotificationResponse;
import com.opu.opube.feature.notification.command.domain.aggregate.MemberNotificationSetting;
import com.opu.opube.feature.notification.command.domain.aggregate.Notification;
import com.opu.opube.feature.notification.command.domain.aggregate.NotificationType;
import com.opu.opube.feature.notification.command.domain.aggregate.NotificationTypeCode;
import com.opu.opube.feature.notification.command.domain.repository.MemberNotificationSettingRepository;
import com.opu.opube.feature.notification.command.domain.repository.NotificationTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.opu.opube.feature.notification.command.domain.repository.NotificationRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationCommandServiceImpl implements NotificationCommandService {

    private final NotificationRepository notificationRepository;
    private final NotificationTypeRepository notificationTypeRepository;
    private final NotificationSseService notificationSseService;
    private final WebPushNotificationService webPushNotificationService;
    private final MemberNotificationSettingRepository memberNotificationSettingRepository;

    @Override
    @Transactional
    public NotificationResponse sendNotification(
            Long memberId,
            NotificationTypeCode typeCode,
            String title,
            String message,
            Integer linkedContentId
    ) {
        String code = typeCode.getCode();
        NotificationType type = notificationTypeRepository.findByCode(code)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.NOTIFICATION_TYPE_NOT_FOUND
                        )
                );

        Notification notification = Notification.builder()
                .title(title)
                .message(message)
                .member(Member.builder().id(memberId).build())
                .notificationType(type)
                .linkedContentId(linkedContentId)
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);

        NotificationResponse dto = NotificationResponse.from(saved);

        notificationSseService.sendToMember(memberId, dto);

        webPushNotificationService.sendToMember(memberId, title, message);

        return dto;
    }

    @Override
    @Transactional
    public void markAsRead(Long memberId, Long notificationId) {
        Notification notification = notificationRepository
                .findByIdAndMember_Id(notificationId, memberId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!Boolean.TRUE.equals(notification.getIsRead())) {
            notification.markAsRead();
        }
    }

    @Override
    @Transactional
    public void markAllAsRead(Long memberId) {
        notificationRepository.markAllAsReadByMemberId(memberId);
    }

    @Override
    @Transactional
    public void updateSetting(Long memberId, String code, boolean enabled) {

        NotificationType type = notificationTypeRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NOTIFICATION_TYPE_NOT_FOUND,
                        "존재하지 않는 알림 코드입니다: " + code
                ));

        MemberNotificationSetting setting = memberNotificationSettingRepository
                .findByMemberIdAndNotificationTypeId(memberId, type.getId())
                .orElseGet(() -> MemberNotificationSetting.builder()
                        .member(Member.builder().id(memberId).build())
                        .notificationType(type)
                        .enabled(enabled)
                        .build()
                );

        setting.changeEnabled(enabled);
        memberNotificationSettingRepository.save(setting);
    }
}