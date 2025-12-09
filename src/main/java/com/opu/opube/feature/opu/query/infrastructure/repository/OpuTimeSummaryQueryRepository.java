package com.opu.opube.feature.opu.query.infrastructure.repository;// feature.opu.query.infrastructure.repository

import com.opu.opube.feature.opu.command.domain.aggregate.QFavoriteOpu;
import com.opu.opube.feature.opu.command.domain.aggregate.QOpu;
import com.opu.opube.feature.opu.query.dto.request.RequiredMinutesRange;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class OpuTimeSummaryQueryRepository {

    private final JPAQueryFactory queryFactory;

    private static final QOpu opu = QOpu.opu;
    private static final QFavoriteOpu favoriteOpu = QFavoriteOpu.favoriteOpu;

    public Map<RequiredMinutesRange, Long> findSharedAndMineSummary(Long memberId) {
        Expression<String> rangeExpr = buildRangeExpression();

        List<Tuple> tuples = queryFactory
                .select(rangeExpr, opu.count())
                .from(opu)
                .where(
                        opu.deletedAt.isNull(),
                        opu.isShared.isTrue()
                                .or(opu.member.id.eq(memberId).and(opu.isShared.isFalse()))
                )
                .groupBy(rangeExpr)
                .fetch();

        return toRangeCountMap(tuples);
    }

    public Map<RequiredMinutesRange, Long> findFavoriteSummary(Long memberId) {
        Expression<String> rangeExpr = buildRangeExpression();

        List<Tuple> tuples = queryFactory
                .select(rangeExpr, opu.count())
                .from(favoriteOpu)
                .join(opu).on(opu.id.eq(favoriteOpu.opu.id))
                .where(
                        favoriteOpu.memberId.eq(memberId),
                        opu.deletedAt.isNull(),
                        opu.isShared.isTrue().or(opu.member.id.eq(memberId))
                )
                .groupBy(rangeExpr)
                .fetch();

        return toRangeCountMap(tuples);
    }

    private Expression<String> buildRangeExpression() {
        return new CaseBuilder()
                .when(opu.requiredMinutes.loe(1)).then(RequiredMinutesRange.MIN_1.name())
                .when(opu.requiredMinutes.loe(5)).then(RequiredMinutesRange.MIN_5.name())
                .when(opu.requiredMinutes.loe(30)).then(RequiredMinutesRange.MIN_30.name())
                .when(opu.requiredMinutes.loe(60)).then(RequiredMinutesRange.HOUR_1.name())
                .when(opu.requiredMinutes.loe(1440)).then(RequiredMinutesRange.DAY_1.name())
                .otherwise(RequiredMinutesRange.TOTAL.name());
    }

    private Map<RequiredMinutesRange, Long> toRangeCountMap(List<Tuple> tuples) {
        Map<RequiredMinutesRange, Long> map =
                new EnumMap<>(RequiredMinutesRange.class);

        // 기본값 0
        for (RequiredMinutesRange r : RequiredMinutesRange.values()) {
            map.put(r, 0L);
        }

        long total = 0L;
        for (Tuple t : tuples) {
            String name = t.get(0, String.class);
            Long count = t.get(1, Long.class);

            RequiredMinutesRange range = RequiredMinutesRange.valueOf(name);
            map.put(range, count);
            total += count;
        }

        map.put(RequiredMinutesRange.TOTAL, total);
        return map;
    }
}