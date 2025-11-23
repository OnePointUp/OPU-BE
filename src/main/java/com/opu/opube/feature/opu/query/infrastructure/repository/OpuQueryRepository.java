package com.opu.opube.feature.opu.query.infrastructure.repository;

import java.util.List;
import com.opu.opube.feature.opu.command.domain.aggregate.Opu;

public interface OpuQueryRepository {
    // 좋아요 한 OPU수
    long countFavoriteOpuByMemberId(Long memberId);

    // 만든 OPU수
    long countMyOpuByMemberId(Long memberId);
}