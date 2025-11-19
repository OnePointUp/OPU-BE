package com.opu.opube.feature.notification.command.application.service;

import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.notification.command.application.dto.response.NotificationResponse;
import com.opu.opube.feature.notification.command.domain.aggregate.Notification;
import com.opu.opube.feature.notification.command.domain.aggregate.NotificationType;
import com.opu.opube.feature.notification.command.domain.aggregate.NotificationTypeCode;
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

        return dto;
    }
}