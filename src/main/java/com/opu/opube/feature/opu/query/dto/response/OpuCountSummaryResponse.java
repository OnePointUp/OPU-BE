package com.opu.opube.feature.opu.query.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OpuCountSummaryResponse {
    private final long favoriteOpuCount;
    private final long myOpuCount;
}