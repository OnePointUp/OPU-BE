package com.opu.opube.feature.opu.query.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.opu.query.dto.response.OpuDailyStatsResponse;
import com.opu.opube.feature.opu.query.dto.response.OpuMonthlyStatsResponse;
import com.opu.opube.feature.opu.query.service.OpuStatsQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/opus")
@Tag(name = "OPU 통계", description = "OPU 월별/일별 통계 조회 API")
public class OpuStatsQueryController {

    private final OpuStatsQueryService opuStatsQueryService;

    @GetMapping("/stats/monthly")
    @Operation(
            summary = "월별 OPU 통계 조회",
            description = """
                    선택한 연/월 기준으로 OPU 통계 정보를 조회합니다.
                    - 월별 달성일 수 (OPU 하나라도 완료한 날 수)
                    - 월별 OPU 완료 횟수
                    - 랜덤 OPU 뽑기 횟수
                    - 많이 완료한 OPU TOP 3
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공적으로 조회됨",
                    content = @Content(
                            schema = @Schema(implementation = OpuMonthlyStatsResponse.class)
                    )
            )
    })
    public ApiResponse<OpuMonthlyStatsResponse> getMonthlyStats(
            @AuthenticationPrincipal
            @Parameter(hidden = true) MemberPrincipal principal,

            @RequestParam
            @Parameter(description = "조회 연도", example = "2025")
            int year,

            @RequestParam
            @Parameter(description = "조회 월", example = "12")
            int month
    ) {
        Long memberId = principal.getMemberId();
        OpuMonthlyStatsResponse response =
                opuStatsQueryService.getMonthlyStats(memberId, year, month);

        return ApiResponse.success(response);
    }

    @GetMapping("/calendar")
    @Operation(
            summary = "OPU 캘린더 통계 조회",
            description = """
                    선택한 연/월 기준으로 일별 OPU 완료 횟수를 조회합니다.
                    프론트에서 캘린더 뷰에 일별 완료 여부를 표시할 때 사용합니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공적으로 조회됨",
                    content = @Content(
                            schema = @Schema(implementation = OpuDailyStatsResponse.class)
                    )
            )
    })
    public ApiResponse<OpuDailyStatsResponse> getOpuCalendarStats(
            @AuthenticationPrincipal
            @Parameter(hidden = true) MemberPrincipal principal,

            @RequestParam
            @Parameter(description = "조회 연도", example = "2025")
            int year,

            @RequestParam
            @Parameter(description = "조회 월", example = "12")
            int month
    ) {
        Long memberId = principal.getMemberId();
        OpuDailyStatsResponse response = opuStatsQueryService.getDailyStats(memberId, year, month);
        return ApiResponse.success(response);
    }
}