package com.opu.opube.feature.opu.query.infrastructure.repository;

import com.opu.opube.feature.opu.command.domain.aggregate.QFavoriteOpu;
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

    private final QOpu opu = QOpu.opu;
    private final QFavoriteOpu favoriteOpu = QFavoriteOpu.favoriteOpu;

    @Override
    public long countFavoriteOpuByMemberId(Long memberId) {
        return queryFactory
                .select(favoriteOpu.id.count())
                .from(favoriteOpu)
                .where(favoriteOpu.member.id.eq(memberId))
                .fetchOne();
    }

    @Override
    public long countMyOpuByMemberId(Long memberId) {
        return queryFactory
                .select(opu.id.count())
                .from(opu)
                .where(opu.member.id.eq(memberId))
                .fetchOne();
    }
}