package com.opu.opube.feature.opu.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "OPU 목록 조회 필터 요청 DTO")
public class OpuListFilterRequest {

    @Schema(
            description = "조회할 OPU 카테고리 ID 목록, 여러 개 전달 가능",
            example = "[1, 3, 5]"
    )
    private List<Long> categoryIds;

    @Schema(
            description = """
                소요 시간(requiredMinutes) 필터
                여러 개 전달 가능
                """,
            example = "[5, 30]"
    )
    private List<Integer> requiredMinutes;

    @Schema(
            description = "검색어(제목, 설명 등을 대상)",
            example = "운동"
    )
    private String search;

    @Schema(
            description = "true일 경우, 찜한 OPU만 조회",
            example = "false"
    )
    private Boolean favoriteOnly;

    @Schema(
            description = """
                정렬 옵션
                """,
            example = "NEWEST"
    )
    private OpuSortOption sort;
}