package com.opu.opube.feature.opu.query.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class OpuSummaryResponse {

    private final Long id;
    private final String emoji;
    private final String title;
    private final String categoryName;
    private final Integer requiredMinutes;
    private final String description;
    private final Boolean favorite;          // 내가 찜했는지
    private final Long myCompletionCount;    // 내가 완료한 횟수
    private final Long favoriteCount;        // 전체 찜 수
    private final String authorNickname;     // 작성자 닉네임

    @QueryProjection
    public OpuSummaryResponse(Long id,
                              String emoji,
                              String title,
                              String categoryName,
                              Integer requiredMinutes,
                              String description,
                              Boolean favorite,
                              Long myCompletionCount,
                              Long favoriteCount,
                              String authorNickname) {
        this.id = id;
        this.emoji = emoji;
        this.title = title;
        this.categoryName = categoryName;
        this.requiredMinutes = requiredMinutes;
        this.description = description;
        this.favorite = favorite != null ? favorite : Boolean.FALSE;
        this.myCompletionCount = myCompletionCount != null ? myCompletionCount : 0L;
        this.favoriteCount = favoriteCount != null ? favoriteCount : 0L;
        this.authorNickname = authorNickname;
    }
}