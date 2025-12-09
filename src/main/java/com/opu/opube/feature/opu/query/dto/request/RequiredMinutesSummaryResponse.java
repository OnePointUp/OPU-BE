package com.opu.opube.feature.opu.query.dto.request;// feature.opu.query.dto

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.Map;

@Builder
@Schema(description = "requiredMinutes 기준 OPU 개수 요약 응답")
public record RequiredMinutesSummaryResponse(
        @Schema(
                description = "requiredMinutes 값(예: 1, 5, 30, 60, 1440 등)과 해당 시간대의 OPU 개수 매핑"
        )
        Map<String, Long> requiredMinutes
) {}