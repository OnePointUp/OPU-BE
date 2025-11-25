package com.opu.opube.feature.opu.query.service;

import com.opu.opube.common.dto.PageResponse;
import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.opu.command.domain.aggregate.Opu;
import com.opu.opube.feature.opu.query.dto.request.OpuListFilterRequest;
import com.opu.opube.feature.opu.query.dto.response.OpuCountSummaryResponse;
import com.opu.opube.feature.opu.query.dto.response.OpuSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.opu.opube.feature.opu.query.infrastructure.repository.OpuQueryRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OpuQueryService {

    private final OpuQueryRepository opuQueryRepository;

    @Transactional(readOnly = true)
    public OpuCountSummaryResponse getOpuCountSummary(Long memberId) {
        long likedCount = opuQueryRepository.countFavoriteOpuByMemberId(memberId);
        long myCount = opuQueryRepository.countMyOpuByMemberId(memberId);

        return OpuCountSummaryResponse.builder()
                .favoriteOpuCount(likedCount)
                .myOpuCount(myCount)
                .build();
    }

    public Opu getOpu(Long opuId) {
        return opuQueryRepository.getOpu(opuId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OPU_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public PageResponse<OpuSummaryResponse> getOpuList(
            Long loginMemberId,
            OpuListFilterRequest filter,
            int page,
            int size
    ) {
        return opuQueryRepository.findOpuList(loginMemberId, filter, page, size);
    }

    @Transactional(readOnly = true)
    public PageResponse<OpuSummaryResponse> getMyOpuList(
            Long loginMemberId,
            OpuListFilterRequest filter,
            int page,
            int size
    ) {
        return opuQueryRepository.findMyOpuList(loginMemberId, filter, page, size);
    }

    @Transactional(readOnly = true)
    public PageResponse<OpuSummaryResponse> getFavoriteOpuList(
            Long loginMemberId,
            OpuListFilterRequest filter,
            int page,
            int size
    ) {
        if (loginMemberId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_USER);
        }

        return opuQueryRepository.findFavoriteOpuList(loginMemberId, filter, page, size);
    }


}