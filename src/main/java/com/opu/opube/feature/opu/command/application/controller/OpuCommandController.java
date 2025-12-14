package com.opu.opube.feature.opu.command.application.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.opu.command.application.dto.request.OpuRegisterDto;
import com.opu.opube.feature.opu.command.application.dto.response.OpuRegisterResponse;
import com.opu.opube.feature.opu.command.application.service.OpuCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "OPU - Command",
        description = "OPU 생성, 공개/비공개 전환, 삭제 등 쓰기 작업 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/opus")
public class OpuCommandController {

    private final OpuCommandService opuCommandService;

    @Operation(
            summary = "OPU 생성",
            description = """
                새로운 OPU를 생성합니다.
                - isShared=true 인 경우, 공개 OPU 중 유사 항목이 있으면
                  409 응답과 함께 추천 OPU 목록을 반환합니다.
                """,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "OPU 생성 성공 (created=true)",
                    content = @Content(
                            schema = @Schema(implementation = OpuRegisterResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "유사한 공개 OPU 존재 (created=false, duplicates 포함)",
                    content = @Content(
                            schema = @Schema(implementation = OpuRegisterResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "요청 값 검증 실패"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "OPU 카테고리를 찾을 수 없음"
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<OpuRegisterResponse>> createOpu(
            @AuthenticationPrincipal MemberPrincipal memberPrincipal,
            @Valid @RequestBody OpuRegisterDto dto
    ) {
        Long memberId = memberPrincipal.getMemberId();
        OpuRegisterResponse result = opuCommandService.registerOpu(dto, memberId);

        if (!result.created()) {
            return ResponseEntity.status(409).body(ApiResponse.success(result));
        }
        return ResponseEntity.ok(ApiResponse.success(result));
    }


    @Operation(
            summary = "OPU 공개 처리",
            description = """
                비공개 OPU를 공개 상태로 전환합니다.
                - 유사한 공개 OPU가 있으면 409와 함께 추천 목록을 반환합니다.
                """,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "공개 처리 성공 (created=true)",
                    content = @Content(
                            schema = @Schema(implementation = OpuRegisterResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "유사한 공개 OPU 존재 (created=false)",
                    content = @Content(
                            schema = @Schema(implementation = OpuRegisterResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "해당 OPU에 대한 권한 없음"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "OPU를 찾을 수 없음"
            )
    })
    @PatchMapping("/{opuId}/share")
    public ResponseEntity<ApiResponse<OpuRegisterResponse>> share(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long opuId
    ) {
        OpuRegisterResponse result = opuCommandService.shareOpu(principal.getMemberId(), opuId);

        if (!result.created()) {
            return ResponseEntity.status(409).body(ApiResponse.success(result));
        }
        return ResponseEntity.ok(ApiResponse.success(result));
    }


    @Operation(
            summary = "OPU 비공개 처리",
            description = """
                    공개 OPU를 비공개 상태로 전환합니다.
                    - 본인이 소유한 OPU만 비공개로 변경할 수 있습니다.
                    """
            ,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "비공개 처리 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "해당 OPU에 대한 권한이 없음 (FORBIDDEN_OPU_ACCESS)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "OPU를 찾을 수 없음 (OPU_NOT_FOUND)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PatchMapping("/{opuId}/unshare")
    public ResponseEntity<ApiResponse<Void>> unshareOpu(
            @AuthenticationPrincipal(expression = "memberId") Long memberId,
            @PathVariable Long opuId
    ) {
        opuCommandService.unshareOpu(memberId, opuId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }


    @Operation(
            summary = "OPU 삭제",
            description = """
                    사용자가 소유한 OPU를 삭제합니다. (Soft delete)
                    - 이미 삭제된 OPU에 대해 다시 호출해도 에러 없이 성공 처리됩니다.
                    - 해당 OPU로 생성된 TODO 연관관계도 함께 정리됩니다.
                    """
            ,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "삭제 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "해당 OPU에 대한 권한이 없음 (FORBIDDEN_OPU_ACCESS)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "OPU를 찾을 수 없음 (OPU_NOT_FOUND)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @DeleteMapping("/{opuId}")
    public ResponseEntity<ApiResponse<Void>> deleteOpu(
            @AuthenticationPrincipal(expression = "memberId") Long memberId,
            @PathVariable Long opuId
    ) {
        opuCommandService.deleteOpu(memberId, opuId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}