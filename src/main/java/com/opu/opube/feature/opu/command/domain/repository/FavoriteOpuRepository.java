package com.opu.opube.feature.opu.command.domain.repository;

import com.opu.opube.feature.opu.command.domain.aggregate.FavoriteOpu;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface FavoriteOpuRepository {

    FavoriteOpu save(FavoriteOpu favorite);

    boolean existsByMemberIdAndOpuId(Long memberId, Long opuId);

    void deleteByMemberIdAndOpuId(Long memberId, Long opuId);

    @Modifying
    @Query("delete from FavoriteOpu f where f.memberId = :memberId")
    void deleteByMemberId(Long memberId);
}
