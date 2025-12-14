package com.opu.opube.feature.opu.command.application.service;

import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.member.query.service.MemberQueryService;
import com.opu.opube.feature.opu.command.application.dto.request.OpuRegisterDto;
import com.opu.opube.feature.opu.command.application.dto.response.OpuDuplicateItem;
import com.opu.opube.feature.opu.command.application.dto.response.OpuRegisterResponse;
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
public class OpuCommandServiceImpl implements OpuCommandService {

    private final OpuRepository opuRepository;
    private final MemberQueryService memberQueryService;
    private final OpuCategoryRepository opuCategoryRepository;
    private final TodoCommandService todoCommandService;

    @Override
    @Transactional
    public OpuRegisterResponse registerOpu(OpuRegisterDto dto, Long memberId) {

        List<OpuDuplicateItem> duplicates = findDuplicatePublicOpuOnRegister(dto);
        if (!duplicates.isEmpty()) {
            return OpuRegisterResponse.duplicated(duplicates);
        }

        Member member = memberQueryService.getMember(memberId);
        OpuCategory category = opuCategoryRepository.getOpuCategoryById(dto.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.OPU_CATEGORY_NOT_FOUND));

        Opu opu = Opu.toEntity(dto, member, category);
        Opu savedOpu = opuRepository.save(opu);

        return OpuRegisterResponse.created(savedOpu.getId());
    }

    @Override
    @Transactional
    public OpuRegisterResponse shareOpu(Long memberId, Long opuId) {
        Opu opu = findOpuAndCheckOwnership(opuId, memberId);

        if (Boolean.TRUE.equals(opu.getIsShared())) {
            return OpuRegisterResponse.created(opu.getId());
        }

        List<OpuDuplicateItem> duplicates = findDuplicatePublicOpuOnShare(opu);
        if (!duplicates.isEmpty()) {
            return OpuRegisterResponse.duplicated(duplicates);
        }

        opu.share();
        return OpuRegisterResponse.created(opu.getId());
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



    private List<OpuDuplicateItem> findDuplicatePublicOpuCore(
            String title,
            Integer minutes,
            Long excludeOpuId
    ) {
        String normalizedInput = normalizeTitle(title);
        if (normalizedInput == null || normalizedInput.isBlank()) {
            return List.of();
        }

        List<Opu> candidates = opuRepository.findSharedByRequiredMinutes(minutes);

        return candidates.stream()
                .filter(o -> excludeOpuId == null || !o.getId().equals(excludeOpuId))
                .filter(o -> normalizeTitle(o.getTitle()).equals(normalizedInput))
                .map(o -> new OpuDuplicateItem(
                        o.getId(),
                        o.getTitle(),
                        o.getRequiredMinutes(),
                        o.getCategory().getId()
                ))
                .limit(10)
                .toList();
    }

    private List<OpuDuplicateItem> findDuplicatePublicOpuOnRegister(OpuRegisterDto dto) {
        if (!Boolean.TRUE.equals(dto.getIsShared())) {
            return List.of();
        }
        return findDuplicatePublicOpuCore(dto.getTitle(), dto.getRequiredMinutes(), null);
    }

    private List<OpuDuplicateItem> findDuplicatePublicOpuOnShare(Opu opu) {
        if (Boolean.TRUE.equals(opu.getIsShared())) {
            return List.of();
        }
        return findDuplicatePublicOpuCore(opu.getTitle(), opu.getRequiredMinutes(), opu.getId());
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