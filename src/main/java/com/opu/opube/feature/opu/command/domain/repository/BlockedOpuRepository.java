package com.opu.opube.feature.opu.command.domain.repository;

import com.opu.opube.feature.opu.command.domain.aggregate.BlockedOpu;
import java.util.List;

public interface BlockedOpuRepository {
    BlockedOpu save(BlockedOpu blocked);

    boolean existsByMemberIdAndOpuId(Long memberId, Long opuId);

    void deleteByMemberIdAndOpuId(Long memberId, Long opuId);

    void deleteByMemberIdAndOpuIdIn(Long memberId, List<Long> opuIds);
}