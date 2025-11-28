package com.opu.opube.feature.opu.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "OPU 랜덤 뽑기 응답 DTO")
public class OpuRandomPickResponse {

    @Schema(description = "OPU ID", example = "101")
    private Long opuId;

    @Schema(description = "OPU 제목", example = "하루 10분 스트레칭")
    private String title;

    @Schema(description = "OPU 설명", example = "아침마다 간단한 스트레칭으로 몸을 깨워보세요.")
    private String description;

    @Schema(description = "OPU 이모지", example = "🧘‍♀️")
    private String emoji;

    @Schema(description = "카테고리 이름", example = "건강")
    private String categoryName;

    @Schema(description = "필요 시간(분)", example = "10")
    private Integer requiredMinutes;

    @Schema(description = "찜 여부", example = "true")
    private boolean favorite;
}