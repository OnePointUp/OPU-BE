package com.opu.opube.feature.opu.query.infrastructure.repository;

import com.opu.opube.common.dto.PageResponse;
import com.opu.opube.feature.member.command.domain.aggregate.QMember;
import com.opu.opube.feature.opu.command.domain.aggregate.Opu;
import com.opu.opube.feature.opu.command.domain.aggregate.QBlockedOpu;
import com.opu.opube.feature.opu.command.domain.aggregate.QFavoriteOpu;
import com.opu.opube.feature.opu.command.domain.aggregate.QMemberOpuCounter;
import com.opu.opube.feature.opu.command.domain.aggregate.QOpu;
import com.opu.opube.feature.opu.command.domain.aggregate.QOpuCategory;
import com.opu.opube.feature.opu.query.dto.request.OpuListFilterRequest;
import com.opu.opube.feature.opu.query.dto.request.OpuSortOption;
import com.opu.opube.feature.opu.query.dto.response.BlockedOpuSummaryResponse;
import com.opu.opube.feature.opu.query.dto.response.OpuSummaryResponse;
import com.opu.opube.feature.opu.query.dto.response.QBlockedOpuSummaryResponse;
import com.opu.opube.feature.opu.query.dto.response.QOpuSummaryResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
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
                .where(favoriteOpu.memberId.eq(memberId))
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
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(opu)
                        .where(opu.id.eq(opuId))
                        .fetchOne()
        );
    }


    @Override
    public PageResponse<OpuSummaryResponse> findOpuList(
            Long loginMemberId,
            OpuListFilterRequest filter,
            int page,
            int size
    ) {
        BooleanBuilder predicate = buildSharedOpuPredicate(loginMemberId, filter);

        OpuSortOption sortOption = filter.getSort();
        if (sortOption == null) {
            sortOption = OpuSortOption.FAVORITE;
        }

        return findOpuPage(loginMemberId, sortOption, predicate, page, size);
    }


    @Override
    public PageResponse<OpuSummaryResponse> findMyOpuList(
            Long loginMemberId,
            OpuListFilterRequest filter,
            int page,
            int size
    ) {
        if (loginMemberId == null) {
            return emptyPage(page, size);
        }

        BooleanBuilder predicate = buildMyOpuPredicate(loginMemberId, filter);

        OpuSortOption sortOption = filter.getSort();
        if (sortOption == null) {
            sortOption = OpuSortOption.NEWEST;
        }

        return findOpuPage(loginMemberId, sortOption, predicate, page, size);
    }


    @Override
    public PageResponse<OpuSummaryResponse> findFavoriteOpuList(
            Long loginMemberId,
            OpuListFilterRequest filter,
            int page,
            int size
    ) {
        if (loginMemberId == null) {
            return emptyPage(page, size);
        }

        BooleanBuilder predicate = new BooleanBuilder()
                .and(favoriteOpu.memberId.eq(loginMemberId))
                .and(opu.deletedAt.isNull());

        applyCommonFilters(predicate, filter);

        predicate.and(
                opu.isShared.isTrue()
                        .or(opu.member.id.eq(loginMemberId))
        );

        predicate.and(
                JPAExpressions
                        .selectOne()
                        .from(blockedOpu)
                        .where(
                                blockedOpu.memberId.eq(loginMemberId),
                                blockedOpu.opu.id.eq(opu.id)
                        )
                        .notExists()
        );

        OpuExpressions expr = buildOpuExpressions(loginMemberId);

        OrderSpecifier<?> orderSpecifier;
        if (filter.getSort() == null) {
            orderSpecifier = favoriteOpu.createdAt.desc();
        } else {
            orderSpecifier = buildOrderSpecifier(filter.getSort(), expr.favoriteCount, expr.myCompletionCount);
        }

        List<OpuSummaryResponse> content = queryFactory
                .select(new QOpuSummaryResponse(
                        opu.id,
                        opu.emoji,
                        opu.title,
                        opu.category.id,
                        category.name,
                        opu.requiredMinutes,
                        opu.description,
                        opu.isShared,
                        expr.isFavorite,
                        expr.myCompletionCount,
                        expr.favoriteCount,
                        member.id,
                        member.nickname,
                        expr.isMine
                ))
                .from(favoriteOpu)
                .join(favoriteOpu.opu, opu)
                .leftJoin(opu.category, category)
                .leftJoin(opu.member, member)
                .where(predicate)
                .orderBy(orderSpecifier)
                .offset((long) page * size)
                .limit(size)
                .fetch();

        Long total = queryFactory
                .select(favoriteOpu.count())
                .from(favoriteOpu)
                .join(favoriteOpu.opu, opu)
                .leftJoin(opu.category, category)
                .leftJoin(opu.member, member)
                .where(predicate)
                .fetchOne();

        return PageResponse.from(content, total == null ? 0L : total, page, size);
    }


    @Override
    public PageResponse<BlockedOpuSummaryResponse> findBlockedOpuList(
            Long loginMemberId,
            OpuListFilterRequest filter,
            int page,
            int size
    ) {
        if (loginMemberId == null) {
            return emptyPage(page, size);
        }

        BooleanBuilder predicate = new BooleanBuilder()
                .and(blockedOpu.memberId.eq(loginMemberId))
                .and(opu.deletedAt.isNull());

        applyCommonFilters(predicate, filter);

        predicate.and(
                opu.isShared.isTrue()
                        .or(opu.member.id.eq(loginMemberId))
        );

        List<BlockedOpuSummaryResponse> content = queryFactory
                .select(new QBlockedOpuSummaryResponse(
                        opu.id,
                        opu.emoji,
                        opu.title,
                        opu.category.id,
                        category.name,
                        opu.requiredMinutes,
                        blockedOpu.createdAt
                ))
                .from(blockedOpu)
                .join(blockedOpu.opu, opu)
                .leftJoin(opu.category, category)
                .where(predicate)
                .orderBy(blockedOpu.createdAt.desc())
                .offset((long) page * size)
                .limit(size)
                .fetch();

        Long total = queryFactory
                .select(blockedOpu.count())
                .from(blockedOpu)
                .join(blockedOpu.opu, opu)
                .leftJoin(opu.category, category)
                .where(predicate)
                .fetchOne();

        return PageResponse.from(content, total == null ? 0L : total, page, size);
    }


    private PageResponse<OpuSummaryResponse> findOpuPage(
            Long loginMemberId,
            OpuSortOption sortOption,
            BooleanBuilder predicate,
            int page,
            int size
    ) {
        OpuExpressions expr = buildOpuExpressions(loginMemberId);
        OrderSpecifier<?> orderSpecifier = buildOrderSpecifier(sortOption, expr.favoriteCount, expr.myCompletionCount);

        List<OpuSummaryResponse> content = queryFactory
                .select(new QOpuSummaryResponse(
                        opu.id,
                        opu.emoji,
                        opu.title,
                        opu.category.id,
                        category.name,
                        opu.requiredMinutes,
                        opu.description,
                        opu.isShared,
                        expr.isFavorite,
                        expr.myCompletionCount,
                        expr.favoriteCount,
                        member.id,
                        member.nickname,
                        expr.isMine
                ))
                .from(opu)
                .leftJoin(opu.category, category)
                .leftJoin(opu.member, member)
                .where(predicate)
                .orderBy(orderSpecifier)
                .offset((long) page * size)
                .limit(size)
                .fetch();

        Long total = queryFactory
                .select(opu.count())
                .from(opu)
                .where(predicate)
                .fetchOne();

        return PageResponse.from(content, total == null ? 0L : total, page, size);
    }


    private void applyCommonFilters(BooleanBuilder predicate, OpuListFilterRequest filter) {
        if (filter.getCategoryIds() != null && !filter.getCategoryIds().isEmpty()) {
            predicate.and(opu.category.id.in(filter.getCategoryIds()));
        }

        if (filter.getRequiredMinutes() != null && !filter.getRequiredMinutes().isEmpty()) {
            predicate.and(opu.requiredMinutes.in(filter.getRequiredMinutes()));
        }

        if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
            String keyword = "%" + filter.getSearch().trim() + "%";
            predicate.and(opu.title.likeIgnoreCase(keyword));
        }
    }


    private BooleanBuilder buildSharedOpuPredicate(Long loginMemberId, OpuListFilterRequest filter) {
        BooleanBuilder predicate = new BooleanBuilder()
                .and(opu.isShared.isTrue())
                .and(opu.deletedAt.isNull());

        applyCommonFilters(predicate, filter);

        if (Boolean.TRUE.equals(filter.getFavoriteOnly()) && loginMemberId != null) {
            predicate.and(
                    JPAExpressions
                            .selectOne()
                            .from(favoriteOpu)
                            .where(
                                    favoriteOpu.memberId.eq(loginMemberId),
                                    favoriteOpu.opu.id.eq(opu.id)
                            )
                            .exists()
            );
        }

        if (loginMemberId != null) {
            predicate.and(
                    JPAExpressions
                            .selectOne()
                            .from(blockedOpu)
                            .where(
                                    blockedOpu.memberId.eq(loginMemberId),
                                    blockedOpu.opu.id.eq(opu.id)
                            )
                            .notExists()
            );
        }

        return predicate;
    }

    private BooleanBuilder buildMyOpuPredicate(Long loginMemberId, OpuListFilterRequest filter) {
        BooleanBuilder predicate = new BooleanBuilder()
                .and(opu.member.id.eq(loginMemberId))
                .and(opu.deletedAt.isNull());

        applyCommonFilters(predicate, filter);

        if (Boolean.TRUE.equals(filter.getFavoriteOnly())) {
            predicate.and(
                    JPAExpressions
                            .selectOne()
                            .from(favoriteOpu)
                            .where(
                                    favoriteOpu.memberId.eq(loginMemberId),
                                    favoriteOpu.opu.id.eq(opu.id)
                            )
                            .exists()
            );
        }

        return predicate;
    }


    private OpuExpressions buildOpuExpressions(Long loginMemberId) {
        Expression<Long> favoriteCount = JPAExpressions
                .select(favoriteOpu.count())
                .from(favoriteOpu)
                .where(favoriteOpu.opu.id.eq(opu.id));

        Expression<Long> myCompletionCount;
        if (loginMemberId == null) {
            myCompletionCount = Expressions.constant(0L);
        } else {
            myCompletionCount = JPAExpressions
                    .select(opuCounter.totalCompletions.longValue())
                    .from(opuCounter)
                    .where(
                            opuCounter.member.id.eq(loginMemberId),
                            opuCounter.opu.id.eq(opu.id)
                    );
        }

        BooleanExpression isFavorite;
        if (loginMemberId == null) {
            isFavorite = Expressions.FALSE;
        } else {
            isFavorite = JPAExpressions
                    .selectOne()
                    .from(favoriteOpu)
                    .where(
                            favoriteOpu.memberId.eq(loginMemberId),
                            favoriteOpu.opu.id.eq(opu.id)
                    )
                    .exists();
        }

        BooleanExpression isMine =
                (loginMemberId == null) ? Expressions.FALSE : opu.member.id.eq(loginMemberId);

        return new OpuExpressions(favoriteCount, myCompletionCount, isFavorite, isMine);
    }


    private OrderSpecifier<?> buildOrderSpecifier(
            OpuSortOption sortOption,
            Expression<Long> favoriteCountExpr,
            Expression<Long> myCompletionCountExpr
    ) {
        return switch (sortOption) {
            case NAME_ASC -> opu.title.asc();
            case COMPLETION -> new OrderSpecifier<>(Order.DESC, myCompletionCountExpr);
            case FAVORITE -> new OrderSpecifier<>(Order.DESC, favoriteCountExpr);
            case NEWEST -> opu.createdAt.desc();
        };
    }

    private <T> PageResponse<T> emptyPage(int page, int size) {
        return PageResponse.from(List.of(), 0L, page, size);
    }

    private static class OpuExpressions {
        private final Expression<Long> favoriteCount;
        private final Expression<Long> myCompletionCount;
        private final BooleanExpression isFavorite;
        private final BooleanExpression isMine;

        private OpuExpressions(
                Expression<Long> favoriteCount,
                Expression<Long> myCompletionCount,
                BooleanExpression isFavorite,
                BooleanExpression isMine
        ) {
            this.favoriteCount = favoriteCount;
            this.myCompletionCount = myCompletionCount;
            this.isFavorite = isFavorite;
            this.isMine = isMine;
        }
    }
}