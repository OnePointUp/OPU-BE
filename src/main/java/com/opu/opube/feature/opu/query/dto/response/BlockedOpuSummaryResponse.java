package com.opu.opube.feature.opu.query.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BlockedOpuSummaryResponse {

    private final Long opuId;
    private final String emoji;
    private final String title;
    private final Long categoryId;
    private final String categoryName;
    private final Integer requiredMinutes;
    private final LocalDateTime blockedAt;

    @QueryProjection
    public BlockedOpuSummaryResponse(
            Long opuId,
            String emoji,
            String title,
            Long categoryId,
            String categoryName,
            Integer requiredMinutes,
            LocalDateTime blockedAt
    ) {
        this.opuId = opuId;
        this.emoji = emoji;
        this.title = title;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.requiredMinutes = requiredMinutes;
        this.blockedAt = blockedAt;
    }
}