package com.opu.opube.feature.notification.command.application.service;

import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.notification.command.domain.aggregate.NotificationType;
import com.opu.opube.feature.notification.command.domain.aggregate.NotificationTypeCode;
import com.opu.opube.feature.notification.command.domain.repository.NotificationTypeRepository;
import com.opu.opube.feature.notification.query.dto.RoutineWeeklyProjection;
import com.opu.opube.feature.notification.query.infrastructure.repository.NotificationScheduleQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDispatchService {

    private final NotificationTypeRepository notificationTypeRepository;
    private final NotificationScheduleQueryRepository scheduleQueryRepository;
    private final NotificationCommandService notificationCommandService;
    private final NotificationMessageFactory messageFactory;

    @Transactional
    public void dispatchTimeMatchedNotifications(List<NotificationTypeCode> typeCodes, LocalTime now) {
        LocalTime nowMin = now.truncatedTo(ChronoUnit.MINUTES);

        for (NotificationTypeCode code : typeCodes) {
            NotificationType type = getType(code);

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
    public void dispatchTodoReminders(LocalTime now) {
        NotificationType todoType = getType(NotificationTypeCode.TODO);

        LocalDate today = LocalDate.now();

        LocalTime base = now.truncatedTo(ChronoUnit.MINUTES);
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
        if (memberIds.isEmpty()) return;

        LocalDate nextWeekStart = LocalDate.now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDate nextWeekEnd = nextWeekStart.plusDays(6);

        List<RoutineWeeklyProjection> routines = scheduleQueryRepository.findRoutinesOverlappingNextWeek(
                memberIds, nextWeekStart, nextWeekEnd
        );

        if (routines.isEmpty()) return;

        Map<Long, List<RoutineWeeklyProjection>> byMember = routines.stream()
                .collect(Collectors.groupingBy(RoutineWeeklyProjection::memberId));

        for (Long memberId : memberIds) {
            List<RoutineWeeklyProjection> myRoutines = byMember.getOrDefault(memberId, List.of());
            if (myRoutines.isEmpty()) continue;

            String title = String.format("다음주에 %d개의 루틴이 있어요 🙂", myRoutines.size());
            String message = buildWeeklyRoutineSummaryMessage(myRoutines, nextWeekStart, nextWeekEnd);

            notificationCommandService.sendNotification(
                    memberId,
                    NotificationTypeCode.ROUTINE,
                    title,
                    message,
                    null
            );
        }
    }

    private String buildWeeklyRoutineSummaryMessage(
            List<RoutineWeeklyProjection> routines,
            LocalDate nextWeekStart,
            LocalDate nextWeekEnd
    ) {
        Map<String, List<String>> grouped = new LinkedHashMap<>();

        for (RoutineWeeklyProjection r : routines) {
            Set<DayOfWeek> days = computeNextWeekOccurrenceDays(r, nextWeekStart, nextWeekEnd);
            if (days.isEmpty()) continue;

            String dayText = formatKorDays(days);
            grouped.computeIfAbsent(dayText, k -> new ArrayList<>()).add(r.title());
        }

        List<String> lines = new ArrayList<>();

        for (var entry : grouped.entrySet()) {
            List<String> titles = entry.getValue();
            titles.sort(Comparator.naturalOrder());

            String titlePart = titles.get(0);
            if (titles.size() >= 2) {
                titlePart = titlePart + " (+ 외 " + (titles.size() - 1) + "개)";
            }

            lines.add("• " + entry.getKey() + ": " + titlePart);
        }

        int totalLines = lines.size();
        List<String> top = lines.stream().limit(3).toList();
        int extra = totalLines - top.size();

        StringBuilder sb = new StringBuilder();
        top.forEach(l -> sb.append(l).append("\n"));
        if (extra > 0) {
            sb.append("(+ 외 ").append(extra).append("개)\n");
        }

        return sb.toString().trim();
    }

    private Set<DayOfWeek> computeNextWeekOccurrenceDays(
            RoutineWeeklyProjection r,
            LocalDate nextWeekStart,
            LocalDate nextWeekEnd
    ) {
        LocalDate start = r.startDate();
        LocalDate end = r.endDate();
        if (start.isAfter(nextWeekEnd) || end.isBefore(nextWeekStart)) return Set.of();

        return switch (r.frequency()) {
            case WEEKLY -> weeklyDays(r.weekDays(), nextWeekStart, nextWeekEnd);
            case DAILY -> EnumSet.allOf(DayOfWeek.class).stream()
                    .filter(d -> isDayInRange(d, nextWeekStart, nextWeekEnd))
                    .collect(Collectors.toCollection(() -> EnumSet.noneOf(DayOfWeek.class)));
            case BIWEEKLY, MONTHLY, YEARLY -> Set.of();
        };
    }

    private Set<DayOfWeek> weeklyDays(String weekDays, LocalDate from, LocalDate to) {
        if (weekDays == null || weekDays.isBlank()) return Set.of();
        Set<Integer> targets = parseIntSet(weekDays);

        EnumSet<DayOfWeek> result = EnumSet.noneOf(DayOfWeek.class);

        LocalDate cursor = from;
        while (!cursor.isAfter(to)) {
            int db = toDbDow0Mon(cursor.getDayOfWeek());
            if (targets.contains(db)) {
                result.add(cursor.getDayOfWeek());
            }
            cursor = cursor.plusDays(1);
        }
        return result;
    }

    private int toDbDow0Mon(DayOfWeek dow) {
        return (dow.getValue() + 6) % 7;
    }

    private boolean isDayInRange(DayOfWeek dow, LocalDate from, LocalDate to) {
        LocalDate cursor = from;
        while (!cursor.isAfter(to)) {
            if (cursor.getDayOfWeek() == dow) return true;
            cursor = cursor.plusDays(1);
        }
        return false;
    }

    private Set<Integer> parseIntSet(String csv) {
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
    }

    private String formatKorDays(Set<DayOfWeek> days) {
        List<DayOfWeek> order = List.of(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY
        );
        List<DayOfWeek> sorted = order.stream().filter(days::contains).toList();

        return sorted.stream()
                .map(this::toKorDay)
                .collect(Collectors.joining("·"));
    }

    private String toKorDay(DayOfWeek d) {
        return switch (d) {
            case MONDAY -> "월";
            case TUESDAY -> "화";
            case WEDNESDAY -> "수";
            case THURSDAY -> "목";
            case FRIDAY -> "금";
            case SATURDAY -> "토";
            case SUNDAY -> "일";
        };
    }

    private NotificationType getType(NotificationTypeCode code) {
        return notificationTypeRepository.findByCode(code.getCode())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NOTIFICATION_TYPE_NOT_FOUND,
                        "알림 타입을 찾을 수 없습니다: " + code
                ));
    }
}