package com.opu.opube.feature.opu.command.domain.repository;

import com.opu.opube.feature.opu.command.domain.aggregate.OpuRandomDrawEvent;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface OpuRandomDrawEventRepository extends Repository<OpuRandomDrawEvent, Long> {
    OpuRandomDrawEvent save(OpuRandomDrawEvent event);

    @Modifying
    @Query("delete from OpuRandomDrawEvent o where o.member.id = :memberId")
    void deleteByMemberId(Long memberId);
}