package com.opu.opube.feature.notifiaction.query.infrastructure.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import com.opu.opube.feature.notifiaction.command.domain.aggregate.Notifiaction;
import com.opu.opube.feature.notifiaction.command.domain.aggregate.QNotifiaction;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotifiactionQueryRepositoryImpl implements NotifiactionQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Notifiaction> findAll() {
        QNotifiaction notifiaction = QNotifiaction.notifiaction;
        return queryFactory
                .selectFrom(notifiaction)
                .fetch();
    }
}