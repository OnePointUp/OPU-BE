package com.opu.opube.feature.member.command.domain.repository;

import com.opu.opube.feature.member.command.domain.aggregate.Member;

import java.util.Optional;

public interface MemberRepository {
    Optional<Member> findById(Long id);
    Optional<Member> findByEmail(String email);
    Optional<Member> findByAuthProviderAndProviderId(String authProvider, String providerId);
    boolean existsByEmail(String email);
    boolean existsByNicknameAndNicknameTag(String nickname, String nicknameTag);
    Member save(Member member);
}