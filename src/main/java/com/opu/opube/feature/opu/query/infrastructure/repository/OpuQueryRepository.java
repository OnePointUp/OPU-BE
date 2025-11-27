package com.opu.opube.feature.opu.query.infrastructure.repository;

import java.util.Optional;

import com.opu.opube.common.dto.PageResponse;
import com.opu.opube.feature.opu.command.domain.aggregate.Opu;
import com.opu.opube.feature.opu.query.dto.request.OpuListFilterRequest;
import com.opu.opube.feature.opu.query.dto.response.BlockedOpuSummaryResponse;
import com.opu.opube.feature.opu.query.dto.response.OpuSummaryResponse;

public interface OpuQueryRepository {
    // 좋아요 한 OPU수
    long countFavoriteOpuByMemberId(Long memberId);

    // 만든 OPU수
    long countMyOpuByMemberId(Long memberId);

    Optional<Opu> getOpu(Long opuId);

    PageResponse<OpuSummaryResponse> findOpuList(
            Long loginMemberId,
            OpuListFilterRequest filter,
            int page,
            int size
    );

    PageResponse<OpuSummaryResponse> findMyOpuList(
            Long loginMemberId,
            OpuListFilterRequest filter,
            int page,
            int size
    );

    PageResponse<OpuSummaryResponse> findFavoriteOpuList(
            Long loginMemberId,
            OpuListFilterRequest filter,
            int page,
            int size
    );

    PageResponse<BlockedOpuSummaryResponse> findBlockedOpuList(
            Long loginMemberId,
            OpuListFilterRequest filter,
            int page,
            int size
    );
}