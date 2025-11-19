package com.opu.opube.feature.notification.query.infrastructure.repository;

import com.opu.opube.feature.notification.command.domain.aggregate.QMemberNotificationSetting;
import com.opu.opube.feature.notification.command.domain.aggregate.QNotificationType;
import com.opu.opube.feature.notification.query.dto.NotificationSettingResponse;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotificationSettingQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<NotificationSettingResponse> findMySettings(Long memberId) {
        QNotificationType type = QNotificationType.notificationType;
        QMemberNotificationSetting setting = QMemberNotificationSetting.memberNotificationSetting;

        BooleanExpression joinOn = setting.notificationType.id.eq(type.id)
                .and(setting.member.id.eq(memberId));

        return queryFactory
                .select(Projections.constructor(
                        NotificationSettingResponse.class,
                        type.id,
                        type.code,
                        type.name,
                        type.description,
                        new CaseBuilder()
                                .when(setting.id.isNotNull()).then((Predicate) setting.enabled)
                                .otherwise(type.defaultEnabled),
                        type.defaultTime
                ))
                .from(type)
                .leftJoin(setting).on(joinOn)
                .orderBy(type.id.asc())
                .fetch();
    }
}