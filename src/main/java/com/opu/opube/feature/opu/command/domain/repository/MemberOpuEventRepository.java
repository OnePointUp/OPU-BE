package com.opu.opube.feature.opu.command.domain.repository;

import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.opu.command.domain.aggregate.MemberOpuEvent;
import com.opu.opube.feature.opu.command.domain.aggregate.Opu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberOpuEventRepository extends JpaRepository<MemberOpuEvent, Long> {

    Optional<MemberOpuEvent> findByMemberAndOpu(Member member, Opu opu);
    Optional<MemberOpuEvent> findTopByMemberAndOpuOrderByCompletedAtDesc(Member member, Opu opu);

    void deleteByMemberId(Long memberId);
}
