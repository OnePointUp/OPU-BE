package com.opu.opube.feature.opu.command.application.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.feature.opu.command.application.service.OpuFavoriteCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/opus")
@RequiredArgsConstructor
@Tag(name = "OPU - Favorite", description = "OPU 찜(즐겨찾기) 관련 API")
public class OpuFavoriteCommandController {

    private final OpuFavoriteCommandService opuFavoriteCommandService;

    @Operation(
            summary = "OPU 찜하기",
            description = """
                    특정 OPU를 찜 목록에 추가합니다.
                    - 이미 찜한 경우에도 에러 없이 성공으로 처리됩니다. (멱등성)
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "찜하기 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 OPU",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/{opuId}/favorite")
    public ApiResponse<Void> favorite(
            @PathVariable Long opuId,
            @AuthenticationPrincipal(expression = "memberId") Long memberId
    ) {
        opuFavoriteCommandService.addFavorite(memberId, opuId);
        return ApiResponse.success(null);
    }

    @Operation(
            summary = "OPU 찜 해제",
            description = """
                    특정 OPU를 찜 목록에서 제거합니다.
                    - 이미 찜이 아닌 경우에도 에러 없이 성공 처리됩니다. (멱등성)
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "찜 해제 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 OPU",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @DeleteMapping("/{opuId}/favorite")
    public ApiResponse<Void> unfavorite(
            @PathVariable Long opuId,
            @AuthenticationPrincipal(expression = "memberId") Long memberId
    ) {
        opuFavoriteCommandService.removeFavorite(memberId, opuId);
        return ApiResponse.success(null);
    }
}