package com.opu.opube.feature.opu.command.application.service;

import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.opu.command.domain.aggregate.BlockedOpu;
import com.opu.opube.feature.opu.command.domain.aggregate.Opu;
import com.opu.opube.feature.opu.command.domain.repository.BlockedOpuRepository;
import com.opu.opube.feature.opu.command.domain.repository.OpuRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpuBlockCommandServiceImpl implements OpuBlockCommandService {

    private final OpuRepository opuRepository;
    private final BlockedOpuRepository blockedOpuRepository;

    @Override
    @Transactional
    public void blockOpu(Long memberId, Long opuId) {
        Opu opu = opuRepository.findById(opuId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OPU_NOT_FOUND));

        if (blockedOpuRepository.existsByMemberIdAndOpuId(memberId, opuId)) {
            return;
        }

        BlockedOpu blocked = BlockedOpu.builder()
                .memberId(memberId)
                .opu(opu)
                .build();

        blockedOpuRepository.save(blocked);
    }

    @Override
    @Transactional
    public void unblockOpu(Long memberId, Long opuId) {
        // 존재하지 않아도 그냥 넘어가도록 (멱등성)
        blockedOpuRepository.deleteByMemberIdAndOpuId(memberId, opuId);
    }
}