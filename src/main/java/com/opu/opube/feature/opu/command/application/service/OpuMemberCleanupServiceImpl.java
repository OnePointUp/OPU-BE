package com.opu.opube.feature.opu.command.application.service;

import com.opu.opube.feature.opu.command.domain.repository.BlockedOpuRepository;
import com.opu.opube.feature.opu.command.domain.repository.FavoriteOpuRepository;
import com.opu.opube.feature.opu.command.domain.repository.MemberOpuCounterRepository;
import com.opu.opube.feature.opu.command.domain.repository.MemberOpuEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OpuMemberCleanupServiceImpl implements OpuMemberCleanupService {

    private final FavoriteOpuRepository favoriteOpuRepository;
    private final BlockedOpuRepository blockedOpuRepository;
    private final MemberOpuEventRepository memberOpuEventRepository;
    private final MemberOpuCounterRepository memberOpuCounterRepository;

    @Override
    @Transactional
    public void deleteByMemberId(Long memberId) {
        favoriteOpuRepository.deleteByMemberId(memberId);
        blockedOpuRepository.deleteByMemberId(memberId);
        memberOpuEventRepository.deleteByMemberId(memberId);
        memberOpuCounterRepository.deleteByMemberId(memberId);
    }
}