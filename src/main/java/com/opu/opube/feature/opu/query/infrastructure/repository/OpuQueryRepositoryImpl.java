package com.opu.opube.feature.opu.query.infrastructure.repository;

import com.opu.opube.common.dto.PageResponse;
import com.opu.opube.feature.member.command.domain.aggregate.QMember;
import com.opu.opube.feature.opu.command.domain.aggregate.*;
import com.opu.opube.feature.opu.query.dto.request.OpuListFilterRequest;
import com.opu.opube.feature.opu.query.dto.response.OpuSummaryResponse;
import com.opu.opube.feature.opu.query.dto.response.QOpuSummaryResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OpuQueryRepositoryImpl implements OpuQueryRepository {

    private final JPAQueryFactory queryFactory;

    private final QOpu opu = QOpu.opu;
    private final QOpuCategory category = QOpuCategory.opuCategory;
    private final QFavoriteOpu favoriteOpu = QFavoriteOpu.favoriteOpu;
    private final QBlockedOpu blockedOpu = QBlockedOpu.blockedOpu;
    private final QMemberOpuCounter opuCounter = QMemberOpuCounter.memberOpuCounter;
    private final QMember member = QMember.member;

    @Override
    public long countFavoriteOpuByMemberId(Long memberId) {
        Long count = queryFactory
                .select(favoriteOpu.id.count())
                .from(favoriteOpu)
                .where(favoriteOpu.member.id.eq(memberId))
                .fetchOne();

        return count != null ? count : 0L;
    }

    @Override
    public long countMyOpuByMemberId(Long memberId) {
        Long count = queryFactory
                .select(opu.id.count())
                .from(opu)
                .where(
                        opu.member.id.eq(memberId),
                        opu.deletedAt.isNull()
                )
                .fetchOne();

        return count != null ? count : 0L;
    }

    @Override
    public Optional<Opu> getOpu(Long opuId) {
        QOpu qOpu = QOpu.opu;

        return Optional.ofNullable(queryFactory
                .selectFrom(qOpu)
                .where(qOpu.id.eq(opuId))
                .fetchOne());
    }

    @Override
    public PageResponse<OpuSummaryResponse> findOpuList(
            Long loginMemberId,
            OpuListFilterRequest filter,
            int page,
            int size
    ) {
        // 공유 OPU + 삭제 안 된 것
        BooleanBuilder predicate = new BooleanBuilder()
                .and(opu.isShared.isTrue())
                .and(opu.deletedAt.isNull());

        // 카테고리 필터
        if (filter.getCategoryIds() != null && !filter.getCategoryIds().isEmpty()) {
            predicate.and(opu.category.id.in(filter.getCategoryIds()));
        }

        // 소요시간 필터
        if (filter.getRequiredMinutes() != null && !filter.getRequiredMinutes().isEmpty()) {
            predicate.and(opu.requiredMinutes.in(filter.getRequiredMinutes()));
        }

        // 검색어 필터 (제목)
        if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
            String keyword = "%" + filter.getSearch().trim() + "%";
            predicate.and(opu.title.likeIgnoreCase(keyword));
        }

        // "찜한 OPU만 보기" 필터
        if (Boolean.TRUE.equals(filter.getFavoriteOnly()) && loginMemberId != null) {
            predicate.and(
                    JPAExpressions
                            .selectOne()
                            .from(favoriteOpu)
                            .where(
                                    favoriteOpu.member.id.eq(loginMemberId),
                                    favoriteOpu.opu.id.eq(opu.id)
                            )
                            .exists()
            );
        }

        // 차단한 OPU 제외
        if (loginMemberId != null) {
            predicate.and(
                    JPAExpressions
                            .selectOne()
                            .from(blockedOpu)
                            .where(
                                    blockedOpu.member.id.eq(loginMemberId),
                                    blockedOpu.opu.id.eq(opu.id)
                            )
                            .notExists()
            );
        }


        // 이 OPU를 찜한 사용자 수
        Expression<Long> favoriteCountExpr =
                JPAExpressions
                        .select(favoriteOpu.count().coalesce(0L))
                        .from(favoriteOpu)
                        .where(favoriteOpu.opu.id.eq(opu.id));

        // 로그인한 사용자가 이 OPU를 완료한 횟수
        Expression<Long> myCompletionCountExpr =
                (loginMemberId == null)
                        ? Expressions.constant(0L)
                        : JPAExpressions
                        .select(
                                opuCounter.totalCompletions.longValue().coalesce(0L)
                        )
                        .from(opuCounter)
                        .where(
                                opuCounter.member.id.eq(loginMemberId),
                                opuCounter.opu.id.eq(opu.id)
                        );

        // 로그인한 사용자가 찜했는지 여부
        BooleanExpression isFavoriteExpr =
                (loginMemberId == null)
                        ? Expressions.FALSE
                        : JPAExpressions
                        .selectOne()
                        .from(favoriteOpu)
                        .where(
                                favoriteOpu.member.id.eq(loginMemberId),
                                favoriteOpu.opu.id.eq(opu.id)
                        )
                        .exists();

        List<OpuSummaryResponse> content = queryFactory
                .select(new QOpuSummaryResponse(
                        opu.id,
                        opu.emoji,
                        opu.title,
                        category.name,
                        opu.requiredMinutes,
                        opu.description,
                        isFavoriteExpr,
                        myCompletionCountExpr,
                        favoriteCountExpr,
                        member.nickname
                ))
                .from(opu)
                .leftJoin(opu.category, category)
                .leftJoin(opu.member, member)
                .where(predicate)
                .orderBy(opu.createdAt.desc())
                .offset((long) page * size)
                .limit(size)
                .fetch();

        // 전체 개수
        Long total = queryFactory
                .select(opu.count())
                .from(opu)
                .where(predicate)
                .fetchOne();

        long totalElements = (total != null) ? total : 0L;

        return PageResponse.from(content, totalElements, page, size);
    }
}