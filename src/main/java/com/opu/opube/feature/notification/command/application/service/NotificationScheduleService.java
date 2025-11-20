package com.opu.opube.feature.notification.command.application.service;

import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.notification.command.domain.aggregate.NotificationType;
import com.opu.opube.feature.notification.command.domain.aggregate.NotificationTypeCode;
import com.opu.opube.feature.notification.command.domain.repository.NotificationTypeRepository;
import com.opu.opube.feature.notification.query.infrastructure.repository.NotificationScheduleQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationScheduleService {

    private final NotificationTypeRepository notificationTypeRepository;
    private final NotificationScheduleQueryRepository scheduleQueryRepository;
    private final NotificationCommandService notificationCommandService;

    @Scheduled(cron = "0 * * * * *") // 매 분 실행
    @Transactional
    public void runScheduledNotifications() {
        LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);

        for (NotificationTypeCode code : List.of(
                NotificationTypeCode.MORNING,
                NotificationTypeCode.EVENING,
                NotificationTypeCode.ROUTINE
        )) {
            processTypeIfTimeMatched(code, now);
        }
    }

    private void processTypeIfTimeMatched(NotificationTypeCode typeCode, LocalTime now) {

        NotificationType type = notificationTypeRepository.findByCode(typeCode.getCode())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NOTIFICATION_TYPE_NOT_FOUND,
                        "알림 타입을 찾을 수 없습니다: " + typeCode
                ));

        NotificationType allType = notificationTypeRepository.findByCode(NotificationTypeCode.ALL.getCode())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NOTIFICATION_TYPE_NOT_FOUND,
                        "ALL 알림 타입을 찾을 수 없습니다."
                ));

        if (!now.equals(type.getDefaultTime().truncatedTo(ChronoUnit.MINUTES))) {
            return;
        }

        List<Long> memberIds = scheduleQueryRepository.findTargetMemberIdsForType(
                type.getId(),
                type.getDefaultEnabled(),
                allType.getId(),
                allType.getDefaultEnabled()
        );

        for (Long memberId : memberIds) {
            notificationCommandService.sendNotification(
                    memberId,
                    typeCode,
                    buildTitle(typeCode),
                    buildMessage(typeCode),
                    null
            );
        }
    }

    private String buildTitle(NotificationTypeCode typeCode) {
        return switch (typeCode) {
            case MORNING -> "OPU와 함께 기분 좋은 하루 시작해요 \uD83C\uDF24️";
            case EVENING -> "오늘을 잘 마무리하셨나요? \uD83C\uDF19";
            case RANDOM_PICK -> "오늘의 랜덤 뽑기가 기다리고 있어요!";
            default -> "알림";
        };
    }

    private String buildMessage(NotificationTypeCode typeCode) {
        return switch (typeCode) {
            case MORNING -> "오늘 일정을 확인하고 계획을 세워보아요.";
            case EVENING -> "오늘 완료하지 못한 일정이 있는지 확인하고 내일 계획을 세워보아요.";
            case RANDOM_PICK -> "OPU를 뽑고 실천하며 오늘도 한 발짝 나아가보아요.";
            default -> "";
        };
    }
}