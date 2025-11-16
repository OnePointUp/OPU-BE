package com.opu.opube.feature.notification.query.infrastructure.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import com.opu.opube.feature.notification.command.domain.aggregate.Notification;
import com.opu.opube.feature.notification.command.domain.aggregate.QNotification;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotificationQueryRepositoryImpl implements NotificationQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Notification> findAll() {
        QNotification notification = QNotification.notification;
        return queryFactory
                .selectFrom(notification)
                .fetch();
    }
}