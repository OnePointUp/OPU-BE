package com.opu.opube.feature.opu.command.application.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.feature.opu.command.application.dto.request.OpuBlockBulkRequest;
import com.opu.opube.feature.opu.command.application.service.OpuBlockCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "OPU - Block",
        description = "OPU 차단/차단 해제 관련 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/opus")
public class OpuBlockCommandController {

    private final OpuBlockCommandService opuBlockCommandService;

    @Operation(
            summary = "OPU 차단",
            description = """
                    특정 OPU를 차단합니다.
                    - 이미 차단된 OPU인 경우에도 에러 없이 그대로 성공으로 처리합니다. (멱등성)
                    """,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "차단 성공",
                    content = @Content(schema = @Schema(implementation = Void.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 OPU",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/{opuId}/block")
    public ApiResponse<Void> blockOpu(
            @AuthenticationPrincipal(expression = "memberId") Long memberId,
            @PathVariable Long opuId
    ) {
        opuBlockCommandService.blockOpu(memberId, opuId);
        return ApiResponse.success(null);
    }

    @Operation(
            summary = "OPU 차단 해제",
            description = """
                    특정 OPU에 대한 차단을 해제합니다.
                    - 차단 상태가 아니어도 에러 없이 그대로 성공으로 처리합니다. (멱등성)
                    """,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "차단 해제 성공",
                    content = @Content(schema = @Schema(implementation = Void.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @DeleteMapping("/{opuId}/block")
    public ApiResponse<Void> unblockOpu(
            @AuthenticationPrincipal(expression = "memberId") Long memberId,
            @PathVariable Long opuId
    ) {
        opuBlockCommandService.unblockOpu(memberId, opuId);
        return ApiResponse.success(null);
    }

    @Operation(
            summary = "OPU 다중 차단 해제",
            description = """
                    여러 OPU에 대한 차단을 한 번에 해제합니다.
                    """,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "다중 차단 해제 성공",
                    content = @Content(schema = @Schema(implementation = Void.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "요청 본문 형식 오류 (opuIds 누락 등)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @DeleteMapping("/blocks")
    public ApiResponse<Void> unblockOpuBulk(
            @AuthenticationPrincipal(expression = "memberId") Long memberId,
            @RequestBody OpuBlockBulkRequest request
    ) {
        opuBlockCommandService.unblockOpuBulk(memberId, request.getOpuIds());
        return ApiResponse.success(null);
    }
}