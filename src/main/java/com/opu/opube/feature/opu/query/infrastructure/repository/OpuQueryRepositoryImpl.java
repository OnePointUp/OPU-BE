package com.opu.opube.feature.opu.query.infrastructure.repository;

import com.opu.opube.common.dto.PageResponse;
import com.opu.opube.feature.member.command.domain.aggregate.QMember;
import com.opu.opube.feature.opu.command.domain.aggregate.*;
import com.opu.opube.feature.opu.query.dto.request.OpuListFilterRequest;
import com.opu.opube.feature.opu.query.dto.request.OpuSortOption;
import com.opu.opube.feature.opu.query.dto.response.OpuSummaryResponse;
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
        return findOpuPage(loginMemberId, filter.getSort(), predicate, page, size);
    }

    @Override
    public PageResponse<OpuSummaryResponse> findMyOpuList(
            Long loginMemberId,
            OpuListFilterRequest filter,
            int page,
            int size
    ) {
        if (loginMemberId == null) {
            return PageResponse.from(List.of(), 0L, page, size);
        }

        BooleanBuilder predicate = buildMyOpuPredicate(loginMemberId, filter);
        return findOpuPage(loginMemberId, filter.getSort(), predicate, page, size);
    }

    @Override
    public PageResponse<OpuSummaryResponse> findFavoriteOpuList(
            Long loginMemberId,
            OpuListFilterRequest filter,
            int page,
            int size
    ) {

        BooleanBuilder predicate = buildFavoriteOpuPredicate(loginMemberId, filter);
        return findOpuPage(loginMemberId, filter.getSort(), predicate, page, size);
    }


    private PageResponse<OpuSummaryResponse> findOpuPage(
            Long loginMemberId,
            OpuSortOption sortOption,
            BooleanBuilder predicate,
            int page,
            int size
    ) {
        // 공통 Expression
        Expression<Long> favoriteCountExpr = buildFavoriteCountExpr();
        Expression<Long> myCompletionCountExpr = buildMyCompletionCountExpr(loginMemberId);
        BooleanExpression isFavoriteExpr = buildIsFavoriteExpr(loginMemberId);
        BooleanExpression isMineExpr = buildIsMineExpr(loginMemberId);

        OrderSpecifier<?> orderSpecifier =
                buildOrderSpecifier(sortOption, favoriteCountExpr, myCompletionCountExpr);

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
                        isFavoriteExpr,
                        myCompletionCountExpr,
                        favoriteCountExpr,
                        member.id,
                        member.nickname,
                        isMineExpr
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

        return PageResponse.from(content, (total == null ? 0 : total), page, size);
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

        // 공통 필터 적용
        applyCommonFilters(predicate, filter);

        // 찜한 OPU만 보기 필터
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

        // 차단한 OPU 제외
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

        // 공통 필터 적용
        applyCommonFilters(predicate, filter);

        // 내 OPU이면서, 찜한 OPU만 보기
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

    private BooleanBuilder buildFavoriteOpuPredicate(Long loginMemberId, OpuListFilterRequest filter) {
        BooleanBuilder predicate = new BooleanBuilder()
                .and(opu.deletedAt.isNull());

        // 공통 필터 적용
        applyCommonFilters(predicate, filter);

        // 내가 찜한 OPU만
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

        // 공개 OPU OR (내가 만든 비공개 OPU)
        predicate.and(
                opu.isShared.isTrue()
                        .or(
                                opu.isShared.isFalse()
                                        .and(opu.member.id.eq(loginMemberId))
                        )
        );

        // 내가 차단한 OPU 제외
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

        return predicate;
    }

    // 찜한 사용자 수
    private Expression<Long> buildFavoriteCountExpr() {
        return JPAExpressions
                .select(favoriteOpu.count())
                .from(favoriteOpu)
                .where(favoriteOpu.opu.id.eq(opu.id));
    }

    // 로그인한 사용자의 완료 횟수
    private Expression<Long> buildMyCompletionCountExpr(Long loginMemberId) {
        if (loginMemberId == null) return Expressions.constant(0L);

        return JPAExpressions
                .select(opuCounter.totalCompletions.longValue())
                .from(opuCounter)
                .where(
                        opuCounter.member.id.eq(loginMemberId),
                        opuCounter.opu.id.eq(opu.id)
                );
    }

    // 로그인한 사용자가 찜했는지 여부
    private BooleanExpression buildIsFavoriteExpr(Long loginMemberId) {
        if (loginMemberId == null) return Expressions.FALSE;

        return JPAExpressions
                .selectOne()
                .from(favoriteOpu)
                .where(
                        favoriteOpu.memberId.eq(loginMemberId),
                        favoriteOpu.opu.id.eq(opu.id)
                )
                .exists();
    }

    // 이 OPU가 내 것인지 여부
    private BooleanExpression buildIsMineExpr(Long loginMemberId) {
        if (loginMemberId == null) return Expressions.FALSE;
        return opu.member.id.eq(loginMemberId);
    }

    //  정렬
    private OrderSpecifier<?> buildOrderSpecifier(
            OpuSortOption sortOption,
            Expression<Long> favoriteCountExpr,
            Expression<Long> myCompletionCountExpr
    ) {
        if (sortOption == null) {
            sortOption = OpuSortOption.NEWEST;
        }

        return switch (sortOption) {
            case NAME_ASC -> opu.title.asc();
            case COMPLETION -> new OrderSpecifier<>(Order.DESC, myCompletionCountExpr);
            case FAVORITE -> new OrderSpecifier<>(Order.DESC, favoriteCountExpr);
            case NEWEST -> opu.createdAt.desc();
        };
    }
}