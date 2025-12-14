package com.opu.opube.feature.notification.query.infrastructure.repository;

import com.opu.opube.feature.member.command.domain.aggregate.QMember;
import com.opu.opube.feature.notification.command.domain.aggregate.QMemberNotificationSetting;
import com.opu.opube.feature.notification.query.dto.TodoNotificationProjection;
import com.opu.opube.feature.notification.query.dto.TodoNotificationResponse;
import com.querydsl.core.types.Projections;
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

    public List<Long> findTargetMemberIdsForType(
            Long typeId,
            boolean defaultEnabled,
            Long allTypeId,
            boolean allDefaultEnabled
    ) {
        QMember member = QMember.member;
        QMemberNotificationSetting setting = QMemberNotificationSetting.memberNotificationSetting;
        QMemberNotificationSetting allSetting = new QMemberNotificationSetting("allSetting");

        return queryFactory
                .select(member.id)
                .from(member)
                // ALL 설정 join
                .leftJoin(allSetting).on(
                        allSetting.member.id.eq(member.id),
                        allSetting.notificationType.id.eq(allTypeId)
                )
                // 타입별 설정 join
                .leftJoin(setting).on(
                        setting.member.id.eq(member.id),
                        setting.notificationType.id.eq(typeId)
                )
                .where(
                        // ALL On 조건
                        (
                                allSetting.id.isNotNull().and(allSetting.enabled.isTrue())
                        ).or(
                                allDefaultEnabled
                                        ? allSetting.id.isNull() // default = true
                                        : allSetting.id.isNotNull().and(allSetting.enabled.isTrue())
                        ),

                        // 해당 타입 On 조건
                        (
                                setting.id.isNotNull().and(setting.enabled.isTrue())
                        ).or(
                                defaultEnabled
                                        ? setting.id.isNull()
                                        : setting.id.isNotNull().and(setting.enabled.isTrue())
                        )
                )
                .fetch();
    }


    public List<TodoNotificationProjection> findTodosForReminder(
            LocalDate date,
            LocalTime timeFrom,
            LocalTime timeTo,
            Long todoTypeId,
            boolean todoDefaultEnabled,
            Long allTypeId,
            boolean allDefaultEnabled
    ) {
        QMember member = QMember.member;
        QMemberNotificationSetting todoSetting = QMemberNotificationSetting.memberNotificationSetting;
        QMemberNotificationSetting allSetting = new QMemberNotificationSetting("allSetting");

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
                .leftJoin(allSetting).on(
                        allSetting.member.id.eq(member.id),
                        allSetting.notificationType.id.eq(allTypeId)
                )
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

                        (
                                allSetting.id.isNotNull().and(allSetting.enabled.isTrue())
                        ).or(
                                allDefaultEnabled
                                        ? allSetting.id.isNull()
                                        : allSetting.enabled.isTrue()
                        ),

                        (
                                todoSetting.id.isNotNull().and(todoSetting.enabled.isTrue())
                        ).or(
                                todoDefaultEnabled
                                        ? todoSetting.id.isNull()
                                        : todoSetting.enabled.isTrue()
                        )
                )
                .fetch()
                .stream()
                .map(it -> (TodoNotificationProjection) it)
                .toList();
    }
}