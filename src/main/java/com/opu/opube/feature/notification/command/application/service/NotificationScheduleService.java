package com.opu.opube.feature.notification.command.application.service;

import com.opu.opube.feature.notification.command.domain.aggregate.NotificationTypeCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationScheduleService {

    private final NotificationDispatchService dispatchService;

    @Scheduled(cron = "0 * * * * *") // 매 분 실행
    public void runScheduledNotifications() {
        LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
        log.info("[Scheduler] runScheduledNotifications started at {}", now);

        dispatchService.dispatchTimeMatchedNotifications(
                List.of(
                        NotificationTypeCode.MORNING,
                        NotificationTypeCode.EVENING,
                        NotificationTypeCode.ROUTINE
                ),
                now
        );

        dispatchService.dispatchTodoReminders(now);
    }

    @Scheduled(cron = "0 0 18 ? * SUN")
    public void processWeeklyRoutineReminder() {
        log.info("[Scheduler] WEEKLY ROUTINE REMINDER triggered");
        dispatchService.dispatchWeeklyRoutineReminder();
    }
}