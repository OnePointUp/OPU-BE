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

import java.util.List;

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
        validateDuplicatePublicOpuOnRegister(dto);

        Member member = memberQueryService.getMember(memberId);
        OpuCategory category = opuCategoryRepository.getOpuCategoryById(dto.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.OPU_CATEGORY_NOT_FOUND));

        Opu opu = Opu.toEntity(dto, member, category);
        Opu savedOpu = opuRepository.save(opu);
        return savedOpu.getId();
    }

    @Override
    @Transactional
    public void shareOpu(Long memberId, Long opuId) {
        Opu opu = findOpuAndCheckOwnership(opuId, memberId);
        validateDuplicatePublicOpuOnShare(opu);
        opu.share();
    }

    @Override
    @Transactional
    public void unshareOpu(Long memberId, Long opuId) {
        Opu opu = findOpuAndCheckOwnership(opuId, memberId);
        opu.unshare();
    }

    private Opu findOpuAndCheckOwnership(Long opuId, Long memberId) {
        Opu opu = opuRepository.findById(opuId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OPU_NOT_FOUND));

        if (!opu.getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_OPU_ACCESS);
        }
        return opu;
    }

    @Override
    @Transactional
    public void deleteOpu(Long memberId, Long opuId) {
        Opu opu = findOpuAndCheckOwnership(opuId, memberId);

        if (opu.isDeleted()) {
            return;
        }

        opu.delete();

        todoCommandService.clearOpuFromTodos(opuId);
    }



    private void validateDuplicatePublicOpuCore(String title, Integer minutes, Long excludeOpuId) {
        String normalizedInput = normalizeTitle(title);

        List<Opu> candidates = opuRepository.findSharedByRequiredMinutes(minutes);

        boolean isDuplicate = candidates.stream()
                .filter(o -> !(excludeOpuId != null && o.getId().equals(excludeOpuId))) // 자기 자신은 건너뜀
                .anyMatch(o -> normalizeTitle(o.getTitle()).equals(normalizedInput));

        if (isDuplicate) {
            throw new BusinessException(ErrorCode.DUPLICATE_OPU);
        }
    }

    private void validateDuplicatePublicOpuOnRegister(OpuRegisterDto dto) {
        if (!Boolean.TRUE.equals(dto.getIsShared())) {
            return;
        }
        validateDuplicatePublicOpuCore(dto.getTitle(), dto.getRequiredMinutes(), null);
    }

    private void validateDuplicatePublicOpuOnShare(Opu opu) {
        if (Boolean.TRUE.equals(opu.getIsShared())) {
            return;
        }

        validateDuplicatePublicOpuCore(
                opu.getTitle(),
                opu.getRequiredMinutes(),
                opu.getId()
        );
    }

    private String normalizeTitle(String title) {
        if (title == null) return null;

        String t = title.trim();

        // 1) 이모지 제거 (유니코드 범위)
        t = t.replaceAll("[\\p{So}\\p{Cn}]", "");

        // 2) 특수문자 제거 (문자, 숫자, 공백만 허용)
        t = t.replaceAll("[^\\p{L}\\p{N} ]+", "");

        // 3) 공백 제거
        t = t.replace(" ", "");

        // 4) lower-case 정규화
        return t.toLowerCase();
    }
}