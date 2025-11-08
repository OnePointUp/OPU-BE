package com.opu.opube.feature.member.query.infrastructure.repository;

import java.util.List;
import com.opu.opube.feature.member.command.domain.aggregate.Member;

public interface MemberQueryRepository {
    List<Member> findAll();
}