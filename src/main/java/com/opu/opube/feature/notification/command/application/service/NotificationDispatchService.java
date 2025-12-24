package com.opu.opube.feature.notification.command.application.service;

import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.notification.command.domain.aggregate.NotificationType;
import com.opu.opube.feature.notification.command.domain.aggregate.NotificationTypeCode;
import com.opu.opube.feature.notification.command.domain.repository.NotificationTypeRepository;
import com.opu.opube.feature.notification.query.infrastructure.repository.NotificationScheduleQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDispatchService {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final NotificationTypeRepository notificationTypeRepository;
    private final NotificationScheduleQueryRepository scheduleQueryRepository;
    private final NotificationCommandService notificationCommandService;
    private final NotificationMessageFactory messageFactory;

    @Transactional
    public void dispatchTimeMatchedNotifications(List<NotificationTypeCode> typeCodes, LocalTime ignoredNow) {
        // KST 기준 현재 시각(분 단위)
        LocalTime nowMin = ZonedDateTime.now(KST).toLocalTime().truncatedTo(ChronoUnit.MINUTES);

        for (NotificationTypeCode code : typeCodes) {
            NotificationType type = getType(code);

            // DB에 저장된 기본 시간도 분 단위로 맞춤
            LocalTime target = type.getDefaultTime().truncatedTo(ChronoUnit.MINUTES);
            if (!nowMin.equals(target)) continue;

            List<Long> memberIds = scheduleQueryRepository.findTargetMemberIdsForType(
                    type.getId(),
                    type.getDefaultEnabled()
            );

            NotificationMessageFactory.NotificationMessage msg = messageFactory.create(code);

            for (Long memberId : memberIds) {
                notificationCommandService.sendNotification(
                        memberId,
                        code,
                        msg.title(),
                        msg.message(),
                        null
                );
            }
        }
    }

    @Transactional
    public void dispatchTodoReminders(LocalTime ignoredNow) {
        NotificationType todoType = getType(NotificationTypeCode.TODO);

        ZonedDateTime nowKst = ZonedDateTime.now(KST).truncatedTo(ChronoUnit.MINUTES);
        LocalDate today = nowKst.toLocalDate();

        LocalTime base = nowKst.toLocalTime();          // 이미 분 단위
        LocalTime timeFrom = base.plusMinutes(10);
        LocalTime timeTo = timeFrom.plusMinutes(1);

        var todos = scheduleQueryRepository.findTodosForReminder(
                today,
                timeFrom,
                timeTo,
                todoType.getId(),
                todoType.getDefaultEnabled()
        );

        for (var todo : todos) {
            notificationCommandService.sendNotification(
                    todo.getMemberId(),
                    NotificationTypeCode.TODO,
                    todo.getTitle(),
                    null,
                    todo.getTodoId()
            );
        }
    }

    @Transactional
    public void dispatchWeeklyRoutineReminder() {
        NotificationType routineType = getType(NotificationTypeCode.ROUTINE);

        List<Long> memberIds = scheduleQueryRepository.findTargetMemberIdsForType(
                routineType.getId(),
                routineType.getDefaultEnabled()
        );

        for (Long memberId : memberIds) {
            notificationCommandService.sendNotification(
                    memberId,
                    NotificationTypeCode.ROUTINE,
                    "다음주 루틴을 확인해보세요",
                    null,
                    null
            );
        }
    }

    private NotificationType getType(NotificationTypeCode code) {
        return notificationTypeRepository.findByCode(code.getCode())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NOTIFICATION_TYPE_NOT_FOUND,
                        "알림 타입을 찾을 수 없습니다: " + code
                ));
    }
}