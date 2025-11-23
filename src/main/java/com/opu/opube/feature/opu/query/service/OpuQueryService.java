package com.opu.opube.feature.opu.query.service;

import com.opu.opube.feature.opu.query.dto.response.OpuCountSummaryResponse;
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
}