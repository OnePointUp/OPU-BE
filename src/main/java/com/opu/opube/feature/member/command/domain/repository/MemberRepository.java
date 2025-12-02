package com.opu.opube.feature.member.command.domain.repository;

import com.opu.opube.feature.member.command.domain.aggregate.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByAuthProviderAndProviderId(String authProvider, String providerId);
    boolean existsByEmail(String email);
    boolean existsByNicknameAndNicknameTag(String nickname, String nicknameTag); // 추가
}