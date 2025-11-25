package com.opu.opube.feature.opu.command.application.service;

import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.opu.command.domain.aggregate.FavoriteOpu;
import com.opu.opube.feature.opu.command.domain.aggregate.Opu;
import com.opu.opube.feature.opu.command.domain.repository.FavoriteOpuRepository;
import com.opu.opube.feature.opu.command.domain.repository.OpuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OpuFavoriteCommandServiceImpl implements OpuFavoriteCommandService {

    private final FavoriteOpuRepository favoriteOpuRepository;
    private final OpuRepository opuRepository;

    @Transactional
    public void addFavorite(Long memberId, Long opuId) {
        Opu opu = opuRepository.findById(opuId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OPU_NOT_FOUND));

        if (favoriteOpuRepository.existsByMemberIdAndOpuId(memberId, opuId)) {
            return;
        }

        FavoriteOpu favorite = FavoriteOpu.builder()
                .memberId(memberId)
                .opu(opu)
                .build();

        favoriteOpuRepository.save(favorite);
    }

    @Transactional
    public void removeFavorite(Long memberId, Long opuId) {
        favoriteOpuRepository.deleteByMemberIdAndOpuId(memberId, opuId);
    }
}