package com.opu.opube.feature.notification.query.infrastructure.repository;

import com.opu.opube.feature.member.command.domain.aggregate.QMember;
import com.opu.opube.feature.notification.command.domain.aggregate.QMemberNotificationSetting;
import com.opu.opube.feature.notification.query.dto.RoutineWeeklyProjection;
import com.opu.opube.feature.notification.query.dto.TodoNotificationProjection;
import com.opu.opube.feature.notification.query.dto.TodoNotificationResponse;
import com.opu.opube.feature.todo.command.domain.aggregate.QRoutine;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.opu.opube.feature.todo.command.domain.aggregate.QTodo.todo;

@Repository
@RequiredArgsConstructor
public class NotificationScheduleQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * - setting 존재: enabled=true인 사람만
     * - setting 없음: defaultEnabled=true면 포함, false면 제외
     */
    public List<Long> findTargetMemberIdsForType(Long typeId, boolean defaultEnabled) {
        QMember member = QMember.member;
        QMemberNotificationSetting setting = QMemberNotificationSetting.memberNotificationSetting;

        BooleanExpression condition = setting.id.isNotNull().and(setting.enabled.isTrue());

        if (defaultEnabled) {
            condition = condition.or(setting.id.isNull());
        }

        return queryFactory
                .select(member.id)
                .from(member)
                .leftJoin(setting).on(
                        setting.member.id.eq(member.id),
                        setting.notificationType.id.eq(typeId)
                )
                .where(condition)
                .fetch();
    }

    /**
     * - todoSetting 존재: enabled=true인 사람만
     * - todoSetting 없음: todoDefaultEnabled=true면 포함, false면 제외
     */
    public List<TodoNotificationProjection> findTodosForReminder(
            LocalDate date,
            LocalTime timeFrom,
            LocalTime timeTo,
            Long todoTypeId,
            boolean todoDefaultEnabled
    ) {
        QMember member = QMember.member;
        QMemberNotificationSetting todoSetting = QMemberNotificationSetting.memberNotificationSetting;

        BooleanExpression todoEnabledCondition =
                todoSetting.id.isNotNull().and(todoSetting.enabled.isTrue());

        if (todoDefaultEnabled) {
            todoEnabledCondition = todoEnabledCondition
                    .or(todoSetting.id.isNull());
        }

        return queryFactory
                .select(Projections.constructor(
                        TodoNotificationResponse.class,
                        todo.member.id,
                        todo.id,
                        todo.title,
                        todo.scheduledDate,
                        todo.scheduledTime
                ))
                .from(todo)
                .join(todo.member, member)
                .leftJoin(todoSetting).on(
                        todoSetting.member.id.eq(member.id),
                        todoSetting.notificationType.id.eq(todoTypeId)
                )
                .where(
                        todo.deletedAt.isNull(),
                        todo.completed.isFalse(),
                        todo.scheduledDate.eq(date),
                        todo.scheduledTime.isNotNull(),
                        todo.scheduledTime.goe(timeFrom),
                        todo.scheduledTime.lt(timeTo),
                        todoEnabledCondition
                )
                .fetch()
                .stream()
                .map(it -> (TodoNotificationProjection) it)
                .toList();
    }

    public List<RoutineWeeklyProjection> findRoutinesOverlappingNextWeek(
            List<Long> memberIds,
            LocalDate nextWeekStart,
            LocalDate nextWeekEnd
    ) {
        QRoutine r = QRoutine.routine;

        return queryFactory
                .select(Projections.constructor(
                        RoutineWeeklyProjection.class,
                        r.member.id,
                        r.id,
                        r.title,
                        r.frequency,
                        r.startDate,
                        r.endDate,
                        r.weekDays,
                        r.monthDays,
                        r.days.as("yearDays")
                ))
                .from(r)
                .where(
                        r.active.isTrue(),
                        r.member.id.in(memberIds),
                        r.startDate.loe(nextWeekEnd),
                        r.endDate.goe(nextWeekStart)
                )
                .fetch();
    }
}