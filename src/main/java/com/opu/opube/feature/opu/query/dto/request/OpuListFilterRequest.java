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
    private OpuSortOption sort = OpuSortOption.NEWEST;
}