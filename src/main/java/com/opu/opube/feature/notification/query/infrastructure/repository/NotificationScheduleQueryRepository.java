package com.opu.opube.feature.notification.query.infrastructure.repository;

import com.opu.opube.feature.member.command.domain.aggregate.QMember;
import com.opu.opube.feature.notification.command.domain.aggregate.QMemberNotificationSetting;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}