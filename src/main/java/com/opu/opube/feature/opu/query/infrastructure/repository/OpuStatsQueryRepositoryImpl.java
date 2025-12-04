package com.opu.opube.feature.opu.query.infrastructure.repository;

import com.opu.opube.feature.opu.query.dto.response.OpuDailyStatsResponse;
import com.opu.opube.feature.opu.query.dto.response.OpuMonthlyStatsResponse;
import com.opu.opube.feature.opu.query.dto.response.QTopCompletedOpuProjection;
import com.opu.opube.feature.opu.query.dto.response.TopCompletedOpuProjection;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.opu.opube.feature.opu.command.domain.aggregate.QMemberOpuEvent.memberOpuEvent;
import static com.opu.opube.feature.opu.command.domain.aggregate.QOpu.opu;
import static com.opu.opube.feature.opu.command.domain.aggregate.QOpuCategory.opuCategory;
import static com.opu.opube.feature.opu.command.domain.aggregate.QOpuRandomDrawEvent.opuRandomDrawEvent;

@Repository
@RequiredArgsConstructor
public class OpuStatsQueryRepositoryImpl implements OpuStatsQueryRepository {

    private final JPAQueryFactory queryFactory;

    public long countCompletedDays(Long memberId, LocalDateTime start, LocalDateTime end) {

        Long result = queryFactory
                .select(
                        Expressions.dateTemplate(
                                LocalDate.class,
                                "DATE({0})",
                                memberOpuEvent.completedAt
                        ).countDistinct()
                )
                .from(memberOpuEvent)
                .where(
                        memberOpuEvent.member.id.eq(memberId),
                        memberOpuEvent.completedAt.goe(start),
                        memberOpuEvent.completedAt.lt(end)
                )
                .fetchOne();

        return result != null ? result : 0L;
    }

    @Override
    public long countCompletedOpus(Long memberId, LocalDateTime start, LocalDateTime end) {
        Long result = queryFactory
                .select(memberOpuEvent.count())
                .from(memberOpuEvent)
                .where(
                        memberOpuEvent.member.id.eq(memberId),
                        memberOpuEvent.completedAt.goe(start),
                        memberOpuEvent.completedAt.lt(end)
                )
                .fetchOne();

        return result != null ? result : 0L;
    }

    @Override
    public long countRandomDraws(Long memberId, LocalDateTime start, LocalDateTime end) {
        Long result = queryFactory
                .select(opuRandomDrawEvent.count())
                .from(opuRandomDrawEvent)
                .where(
                        opuRandomDrawEvent.member.id.eq(memberId),
                        opuRandomDrawEvent.drawnAt.goe(start),
                        opuRandomDrawEvent.drawnAt.lt(end)
                )
                .fetchOne();

        return result != null ? result : 0L;
    }

    @Override
    public List<OpuMonthlyStatsResponse.TopCompletedOpu> findTopCompletedOpus(
            Long memberId,
            LocalDateTime start,
            LocalDateTime end,
            int limit
    ) {
        List<TopCompletedOpuProjection> projections = queryFactory
                .select(new QTopCompletedOpuProjection(
                        opu.id,
                        opu.title,
                        opu.emoji,
                        opuCategory.name,
                        opu.requiredMinutes,
                        memberOpuEvent.count()
                ))
                .from(memberOpuEvent)
                .join(memberOpuEvent.opu, opu)
                .leftJoin(opu.category, opuCategory)
                .where(
                        memberOpuEvent.member.id.eq(memberId),
                        memberOpuEvent.completedAt.goe(start),
                        memberOpuEvent.completedAt.lt(end)
                )
                .groupBy(
                        opu.id,
                        opu.title,
                        opu.emoji,
                        opuCategory.name,
                        opu.requiredMinutes
                )
                .orderBy(memberOpuEvent.count().desc())
                .limit(limit)
                .fetch();

        return projections.stream()
                .map(p -> new OpuMonthlyStatsResponse.TopCompletedOpu(
                        p.getOpuId(),
                        p.getTitle(),
                        p.getEmoji(),
                        p.getCategoryName(),
                        p.getRequiredMinutes(),
                        p.getCompletedCount()
                ))
                .toList();
    }


    @Override
    public List<OpuDailyStatsResponse.DayStat> findDailyCompletedCounts(
            Long memberId,
            LocalDateTime start,
            LocalDateTime end
    ) {
        var completedDateExpr = Expressions.dateTemplate(
                java.sql.Date.class,
                "DATE({0})",
                memberOpuEvent.completedAt
        );

        var countExpr = memberOpuEvent.count();

        List<com.querydsl.core.Tuple> rows = queryFactory
                .select(completedDateExpr, countExpr)
                .from(memberOpuEvent)
                .where(
                        memberOpuEvent.member.id.eq(memberId),
                        memberOpuEvent.completedAt.goe(start),
                        memberOpuEvent.completedAt.lt(end)
                )
                .groupBy(completedDateExpr)
                .orderBy(completedDateExpr.asc())
                .fetch();

        return rows.stream()
                .map(row -> {
                    java.sql.Date sqlDate = row.get(completedDateExpr);
                    LocalDate localDate = sqlDate.toLocalDate();

                    Long count = row.get(countExpr);
                    return new OpuDailyStatsResponse.DayStat(
                            localDate,
                            count != null ? count : 0L
                    );
                })
                .toList();
    }
}