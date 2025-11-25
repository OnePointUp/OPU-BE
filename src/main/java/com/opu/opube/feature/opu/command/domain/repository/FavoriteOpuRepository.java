package com.opu.opube.feature.opu.command.domain.repository;

import com.opu.opube.feature.opu.command.domain.aggregate.FavoriteOpu;

public interface FavoriteOpuRepository {

    FavoriteOpu save(FavoriteOpu favorite);

    boolean existsByMemberIdAndOpuId(Long memberId, Long opuId);

    void deleteByMemberIdAndOpuId(Long memberId, Long opuId);
}
