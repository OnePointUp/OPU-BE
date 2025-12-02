package com.opu.opube.feature.opu.query.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.common.dto.PageResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.opu.query.dto.request.OpuListFilterRequest;
import com.opu.opube.feature.opu.query.dto.request.OpuRandomSource;
import com.opu.opube.feature.opu.query.dto.response.BlockedOpuSummaryResponse;
import com.opu.opube.feature.opu.query.dto.response.OpuSummaryResponse;
import com.opu.opube.feature.opu.query.service.OpuQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "OPU - Query",
        description = "OPU 조회 관련 API (공유 OPU, 내 OPU, 찜/차단 목록, 랜덤 뽑기 등)"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/opus")
public class OpuQueryController {

    private final OpuQueryService opuQueryService;

    @Operation(
            summary = "공유 OPU 목록 조회",
            description = """
                    공유된 OPU 목록을 조회합니다.
                    필터와 페이지네이션을 지원합니다.
                    """,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "공유 OPU 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            )
    })
    @GetMapping
    public ApiResponse<PageResponse<OpuSummaryResponse>> getSharedOpus(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Parameter(
                    description = "필터 조건 (카테고리, 키워드, 정렬 등)",
                    schema = @Schema(implementation = OpuListFilterRequest.class)
            )
            @ModelAttribute OpuListFilterRequest filter,
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        Long loginMemberId = principal.getMemberId();

        PageResponse<OpuSummaryResponse> result =
                opuQueryService.getOpuList(loginMemberId, filter, page, size);

        return ApiResponse.success(result);
    }

    @Operation(
            summary = "내가 만든 OPU 목록 조회",
            description = """
                    로그인한 사용자가 직접 만든 OPU 목록을 조회합니다.
                    필터와 페이지네이션을 지원합니다.
                    """,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "내 OPU 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            )
    })
    @GetMapping("/my")
    public ApiResponse<PageResponse<OpuSummaryResponse>> getMyOpuList(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Parameter(
                    description = "OPU 목록 필터 조건",
                    schema = @Schema(implementation = OpuListFilterRequest.class)
            )
            @ModelAttribute OpuListFilterRequest filter,
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        Long loginMemberId = principal.getMemberId();

        PageResponse<OpuSummaryResponse> result =
                opuQueryService.getMyOpuList(loginMemberId, filter, page, size);

        return ApiResponse.success(result);
    }

    @Operation(
            summary = "찜한 OPU 목록 조회",
            description = """
                    로그인한 사용자가 '찜하기'한 OPU 목록을 조회합니다.
                    필터와 페이지네이션을 지원합니다.
                    """,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "찜한 OPU 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            )
    })
    @GetMapping("/favorites")
    public ApiResponse<PageResponse<OpuSummaryResponse>> getFavoriteOpus(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Parameter(
                    description = "OPU 목록 필터 조건",
                    schema = @Schema(implementation = OpuListFilterRequest.class)
            )
            @ModelAttribute OpuListFilterRequest filter,
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        Long loginMemberId = principal.getMemberId();

        PageResponse<OpuSummaryResponse> result =
                opuQueryService.getFavoriteOpuList(loginMemberId, filter, page, size);

        return ApiResponse.success(result);
    }

    @Operation(
            summary = "차단한 OPU 목록 조회",
            description = """
                    로그인한 사용자가 차단한 OPU 목록을 조회합니다.
                    필터와 페이지네이션을 지원합니다.
                    """,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "차단한 OPU 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            )
    })
    @GetMapping("/blocked")
    public ApiResponse<PageResponse<BlockedOpuSummaryResponse>> getBlockedOpus(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Parameter(
                    description = "OPU 목록 필터 조건",
                    schema = @Schema(implementation = OpuListFilterRequest.class)
            )
            @ModelAttribute OpuListFilterRequest filter,
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        Long loginMemberId = principal.getMemberId();

        PageResponse<BlockedOpuSummaryResponse> result =
                opuQueryService.getBlockedOpuList(loginMemberId, filter, page, size);

        return ApiResponse.success(result);
    }

    @Operation(
            summary = "OPU 랜덤 뽑기",
            description = """
                - `source = ALL` : 전체 공유된 OPU 중에서 랜덤 뽑기\n
                - `source = FAVORITE` : 내가 찜한 OPU 중에서 랜덤 뽑기\n
                - `requiredMinutes` : 소요 시간(분) 필터, 값이 없으면 전체 대상에서 뽑기
                """,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "랜덤 뽑기 성공",
                    content = @Content(schema = @Schema(implementation = OpuSummaryResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "조건에 맞는 OPU를 찾을 수 없음"
            )
    })
    @GetMapping("/random")
    public ResponseEntity<ApiResponse<OpuSummaryResponse>> pickRandomOpu(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Parameter(
                    description = """
                        랜덤 뽑기 대상
                        - ALL      : 전체 공유된 OPU에서 뽑기
                        - FAVORITE : 내가 찜한 OPU에서만 뽑기
                        """,
                    example = "ALL",
                    schema = @Schema(implementation = OpuRandomSource.class)
            )
            @RequestParam(name = "source", defaultValue = "ALL") OpuRandomSource source,
            @Parameter(
                    description = """
                        소요 시간(분) 필터
                        """,
                    example = "5"
            )
            @RequestParam(name = "requiredMinutes", required = false) Integer requiredMinutes,
            @Parameter(
                    description = "직전에 뽑은 OPU의 ID. 전달하면 해당 OPU는 이번 랜덤 대상에서 제외됩니다.",
                    example = "5"
            )
            @RequestParam(name = "excludeOpuId", required = false) Long excludeOpuId
    ) {
        Long memberId = principal.getMemberId();

        OpuSummaryResponse result =
                opuQueryService.pickRandomOpu(memberId, source, requiredMinutes, excludeOpuId);

        return ResponseEntity.ok(ApiResponse.success(result));
    }
}