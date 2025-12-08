package com.opu.opube.feature.opu.command.domain.repository;

import com.opu.opube.feature.opu.command.domain.aggregate.OpuRandomDrawEvent;
import org.springframework.data.repository.Repository;

public interface OpuRandomDrawEventRepository extends Repository<OpuRandomDrawEvent, Long> {
    OpuRandomDrawEvent save(OpuRandomDrawEvent event);

    void deleteByMemberId(Long memberId);
}