package com.opu.opube.feature.member.command.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.member.command.domain.repository.MemberRepository;

public interface JpaMemberRepository extends MemberRepository, JpaRepository<Member, Long> {
}