package com.opu.opube.feature.opu.query.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.opu.query.dto.request.RequiredMinutesSummaryResponse;
import com.opu.opube.feature.opu.query.service.OpuTimeSummaryQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "OPU 시간별 개수",
        description = "시간(requiredMinutes) 기준 OPU 개수 조회 API"
)
@RestController
@RequestMapping("/api/v1/opus/time-summary")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class OpuTimeSummaryQueryController {

    private final OpuTimeSummaryQueryService service;

    @Operation(
            summary = "공유 + 내 비공유 OPU 시간별 개수 조회",
            description = """
                    로그인한 사용자를 기준으로,
                    - 공유된 OPU
                    - 내가 만든 비공유 OPU
                    를 모두 포함해서 requiredMinutes(1분, 5분, 30분, 1시간, 1일 등)별 개수를 집계합니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "요약 조회 성공",
                    content = @Content(schema = @Schema(implementation = RequiredMinutesSummaryResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 또는 토큰 누락",
                    content = @Content(schema = @Schema(implementation = com.opu.opube.common.dto.ApiResponse.class))
            )
    })
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<RequiredMinutesSummaryResponse>> getTotal(
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        Long memberId = principal.getMemberId();
        return ResponseEntity.ok(
                ApiResponse.success(service.getAllSummary(memberId))
        );
    }

    @Operation(
            summary = "찜한 OPU 시간별 개수 조회",
            description = """
                    로그인한 사용자를 기준으로,
                    - 공유된 OPU 중 내가 찜한 OPU
                    - 내가 만든 비공유 OPU 중 내가 찜한 OPU
                    에 대해 requiredMinutes(1분, 5분, 30분, 1시간, 1일 등)별 개수를 집계합니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "요약 조회 성공",
                    content = @Content(schema = @Schema(implementation = RequiredMinutesSummaryResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 또는 토큰 누락",
                    content = @Content(schema = @Schema(implementation = com.opu.opube.common.dto.ApiResponse.class))
            )
    })
    @GetMapping("/favorite")
    public ResponseEntity<ApiResponse<RequiredMinutesSummaryResponse>> getFavorite(
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        Long memberId = principal.getMemberId();
        return ResponseEntity.ok(
                ApiResponse.success(service.getFavoriteSummary(memberId))
        );
    }
}