package com.opu.opube.feature.opu.query.infrastructure.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import com.opu.opube.feature.opu.command.domain.aggregate.Opu;
import com.opu.opube.feature.opu.command.domain.aggregate.QOpu;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OpuQueryRepositoryImpl implements OpuQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Opu> findAll() {
        QOpu opu = QOpu.opu;
        return queryFactory
                .selectFrom(opu)
                .fetch();
    }
}