package com.opu.opube.feature.opu.query.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class TopCompletedOpuProjection extends OpuMonthlyStatsResponse.TopCompletedOpu {

    @QueryProjection
    public TopCompletedOpuProjection(Long opuId,
                                     String title,
                                     String emoji,
                                     String categoryName,
                                     Integer requiredMinutes,
                                     long completedCount) {
        super(opuId, title, emoji, categoryName, requiredMinutes, completedCount);
    }
}