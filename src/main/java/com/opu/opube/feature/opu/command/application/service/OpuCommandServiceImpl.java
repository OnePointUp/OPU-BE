package com.opu.opube.feature.opu.command.application.service;

import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.member.query.service.MemberQueryService;
import com.opu.opube.feature.opu.command.application.dto.request.OpuRegisterDto;
import com.opu.opube.feature.opu.command.domain.aggregate.Opu;
import com.opu.opube.feature.opu.command.domain.aggregate.OpuCategory;
import com.opu.opube.feature.opu.command.domain.repository.OpuCategoryRepository;
import com.opu.opube.feature.todo.command.application.service.TodoCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.opu.opube.feature.opu.command.domain.repository.OpuRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class OpuCommandServiceImpl implements OpuCommandService {

    private final OpuRepository opuRepository;
    private final MemberQueryService memberQueryService;
    private final OpuCategoryRepository opuCategoryRepository;
    private final TodoCommandService todoCommandService;

    @Override
    public Long registerOpu(OpuRegisterDto dto, Long memberId) {
        Member member = memberQueryService.getMember(memberId);
        OpuCategory category = opuCategoryRepository.getOpuCategoryById(dto.categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OPU_CATEGORY_NOT_FOUND));

        Opu opu = Opu.toEntity(dto, member, category);
        Opu savedOpu = opuRepository.save(opu);
        return savedOpu.getId();
    }

    @Override
    @Transactional
    public void shareOpu(Long memberId, Long opuId) {
        Opu opu = opuRepository.findById(opuId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OPU_NOT_FOUND));

        if (!opu.getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_OPU_ACCESS);
        }

        if (Boolean.TRUE.equals(opu.getIsShared())) {
            return;
        }

        opu.share();
    }

    @Override
    @Transactional
    public void unshareOpu(Long memberId, Long opuId) {
        Opu opu = opuRepository.findById(opuId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OPU_NOT_FOUND));

        if (!opu.getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_OPU_ACCESS);
        }

        if (Boolean.FALSE.equals(opu.getIsShared())) {
            return;
        }

        opu.unshare();
    }

    @Override
    public void deleteOpu(Long memberId, Long opuId) {
        Opu opu = opuRepository.findById(opuId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OPU_NOT_FOUND));

        if (!opu.getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_OPU_ACCESS);
        }

        if (opu.isDeleted()) {
            return;
        }

        opu.delete();

        todoCommandService.clearOpuFromTodos(opuId);
    }
}