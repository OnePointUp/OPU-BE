package com.opu.opube.feature.opu.command.domain.repository;

import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.opu.command.domain.aggregate.MemberOpuCounter;
import com.opu.opube.feature.opu.command.domain.aggregate.Opu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

public interface MemberOpuCounterRepository extends JpaRepository<MemberOpuCounter, Long> {
    boolean existsMemberOpuCounterByMemberAndOpu(Member member, Opu opu);

    @Lock(PESSIMISTIC_WRITE)
    Optional<MemberOpuCounter> findByMemberAndOpu(Member member, Opu opu);

    void deleteByMemberId(Long memberId);
}
