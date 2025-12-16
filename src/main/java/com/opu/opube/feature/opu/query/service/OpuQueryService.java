package com.opu.opube.feature.opu.query.service;

import com.opu.opube.common.dto.PageResponse;
import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.opu.command.domain.aggregate.Opu;
import com.opu.opube.feature.opu.command.domain.aggregate.OpuRandomDrawEvent;
import com.opu.opube.feature.opu.command.domain.repository.OpuRandomDrawEventRepository;
import com.opu.opube.feature.opu.query.dto.request.OpuListFilterRequest;
import com.opu.opube.feature.opu.query.dto.request.OpuRandomSource;
import com.opu.opube.feature.opu.query.dto.response.BlockedOpuSummaryResponse;
import com.opu.opube.feature.opu.query.dto.response.OpuCategoryDto;
import com.opu.opube.feature.opu.query.dto.response.OpuCountSummaryResponse;
import com.opu.opube.feature.opu.query.dto.response.OpuSummaryResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.opu.opube.feature.opu.query.infrastructure.repository.OpuQueryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OpuQueryService {

    private final OpuQueryRepository opuQueryRepository;
    private final OpuRandomDrawEventRepository opuRandomDrawEventRepository;
    private final EntityManager entityManager;

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

    @Transactional(readOnly = true)
    public PageResponse<BlockedOpuSummaryResponse> getBlockedOpuList(
            Long loginMemberId,
            OpuListFilterRequest filter,
            int page,
            int size
    ) {
        return opuQueryRepository.findBlockedOpuList(loginMemberId, filter, page, size);
    }


    public OpuSummaryResponse drawRandomOpu(
            Long memberId,
            OpuRandomSource source,
            Integer requiredMinutes,
            Long excludeOpuId
    ) {
        Optional<OpuSummaryResponse> optional = switch (source) {
            case FAVORITE -> opuQueryRepository
                    .drawRandomOpuFromFavorite(memberId, requiredMinutes, excludeOpuId);
            case ALL -> opuQueryRepository
                    .drawRandomOpuFromAll(memberId, requiredMinutes, excludeOpuId);
        };

        OpuSummaryResponse drawn = optional.orElse(null);

        var memberRef = entityManager.getReference(Member.class, memberId);
        Opu opuRef = null;
        if (drawn != null && drawn.getId() != null) {
            opuRef = entityManager.getReference(Opu.class, drawn.getId());
        }

        OpuRandomDrawEvent event = OpuRandomDrawEvent.builder()
                .member(memberRef)
                .opu(opuRef)                     // 실패한 뽑기면 null 저장
                .source(source)
                .requiredMinutes(requiredMinutes)
                .drawnAt(LocalDateTime.now())
                .build();

        opuRandomDrawEventRepository.save(event);



        return optional.orElseThrow(() -> new BusinessException(ErrorCode.OPU_NOT_FOUND));


    }

    public List<OpuCategoryDto> getOpuCategories() {
        return opuQueryRepository.getOpuCategories();
    }
}