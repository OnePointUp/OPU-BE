package com.opu.opube.feature.notification.command.application.service;

import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.notification.command.application.dto.response.NotificationResponse;
import com.opu.opube.feature.notification.command.domain.aggregate.Notification;
import com.opu.opube.feature.notification.command.domain.aggregate.NotificationType;
import com.opu.opube.feature.notification.command.domain.aggregate.NotificationTypeCode;
import com.opu.opube.feature.notification.command.domain.repository.NotificationTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.opu.opube.feature.notification.command.domain.repository.NotificationRepository;

@Service
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
        // 1) enum → code 문자열
        String code = typeCode.getCode();

        // 2) DB에서 NotificationType 조회
        NotificationType type = notificationTypeRepository.findByCode(code)
                .orElseThrow(() ->
                        new IllegalStateException("알림 타입을 찾을 수 없습니다: " + code));

        // 3) 엔티티 생성
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

        // 4) SSE 전송
        notificationSseService.sendToMember(memberId, dto);

        return dto;
    }
}