package com.opu.opube.feature.member.command.domain.repository;

import java.util.UUID;
import com.opu.opube.feature.member.command.domain.aggregate.Member;

public interface MemberRepository {
    Member save(Member member);
}