package com.opu.opube.feature.member.query.infrastructure.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.member.command.domain.aggregate.QMember;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepositoryImpl implements MemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Member> findAll() {
        QMember member = QMember.member;
        return queryFactory
                .selectFrom(member)
                .fetch();
    }
}