package com.opu.opube.feature.notification.query.infrastructure.repository;

import com.opu.opube.feature.notification.command.application.dto.response.NotificationResponse;
import com.opu.opube.feature.notification.command.domain.aggregate.QNotification;
import com.opu.opube.feature.notification.command.domain.aggregate.QNotificationType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotificationQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<NotificationResponse> findMyNotifications(
            Long memberId,
            boolean onlyUnread,
            Pageable pageable
    ) {
        QNotification notification = QNotification.notification;
        QNotificationType notificationType = QNotificationType.notificationType;

        // 공통 where 조건
        BooleanExpression condition = notification.member.id.eq(memberId);
        if (onlyUnread) {
            condition = condition.and(notification.isRead.isFalse());
        }

        List<NotificationResponse> content = queryFactory
                .select(Projections.constructor(
                        NotificationResponse.class,
                        notification.id,
                        notification.notificationType.code,
                        notification.title,
                        notification.message,
                        notification.linkedContentId,
                        notification.isRead,
                        notification.createdAt
                ))
                .from(notification)
                .join(notification.notificationType, notificationType)
                .where(condition)
                .orderBy(notification.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(notification.count())
                .from(notification)
                .where(condition)
                .fetchOne();

        long totalCount = total != null ? total : 0L;

        return new PageImpl<>(content, pageable, totalCount);
    }

    public long countUnread(Long memberId) {
        QNotification notification = QNotification.notification;

        Long count = queryFactory
                .select(notification.count())
                .from(notification)
                .where(
                        notification.member.id.eq(memberId),
                        notification.isRead.isFalse()
                )
                .fetchOne();

        return count != null ? count : 0L;
    }
}