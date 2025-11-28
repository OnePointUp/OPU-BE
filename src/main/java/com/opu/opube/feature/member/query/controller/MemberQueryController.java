package com.opu.opube.feature.member.query.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.member.command.application.dto.response.MemberProfileResponse;
import com.opu.opube.feature.member.query.dto.response.MemberSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.opu.opube.feature.member.query.service.MemberQueryService;

@Tag(
        name = "Member - Query",
        description = "내 프로필 조회 및 마이페이지 요약 정보 조회 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members/me")
public class MemberQueryController {

    private final MemberQueryService memberQueryService;

    @Operation(
            summary = "내 프로필 조회",
            description = """
                    로그인한 사용자의 상세 프로필 정보를 조회합니다.
                    - 이메일, 닉네임, 소개글(Bio), 프로필 이미지 URL 등을 반환합니다.
                    """,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "프로필 조회 성공",
                    content = @Content(schema = @Schema(implementation = MemberProfileResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "회원 정보를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<MemberProfileResponse>> getMyProfile(
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        Long memberId = principal.getMemberId();
        MemberProfileResponse res = memberQueryService.getMyProfile(memberId);
        return ResponseEntity.ok(ApiResponse.success(res));
    }


    @Operation(
            summary = "내 요약 정보 조회",
            description = """
                    마이페이지 상단에 표시될 내 요약 정보를 조회합니다.
                    - 기본 프로필 정보(닉네임, 이메일, 프로필 이미지)
                    - OPU 관련 요약 정보(내가 만든 OPU 개수, 찜한 OPU 개수 등)를 함께 반환합니다.
                    """,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "요약 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = MemberSummaryResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "회원 정보를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<MemberSummaryResponse>> getMySummary(
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        Long memberId = principal.getMemberId();
        MemberSummaryResponse response = memberQueryService.getMemberSummary(memberId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}