package com.opu.opube.feature.opu.query.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OpuListFilterRequest {

    private List<Long> categoryIds;
    private List<Integer> requiredMinutes;
    private String search;
    private Boolean favoriteOnly;

    /**
     * 정렬 옵션
     * newest        = 최신순(default)
     * name_asc      = 이름 오름차순 (가나다순)
     * completion    = 완료 많은순
     * favorite      = 찜 많은 순
     */
    private String sort = "newest";
}