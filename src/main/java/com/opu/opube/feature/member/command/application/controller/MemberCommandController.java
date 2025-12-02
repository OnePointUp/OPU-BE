package com.opu.opube.feature.member.command.application.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.auth.command.application.service.AuthService;
import com.opu.opube.feature.member.command.application.dto.request.MemberDeactivateRequest;
import com.opu.opube.feature.member.command.application.dto.request.UpdateMemberProfileRequest;
import com.opu.opube.feature.member.command.application.dto.request.WebPushAgreeRequest;
import com.opu.opube.feature.member.command.application.dto.response.MemberProfileResponse;
import com.opu.opube.feature.member.command.application.service.MemberCommandService;
import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.member.query.service.MemberQueryService;
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

@Tag(
        name = "Member",
        description = "회원 정보 관련 API"
)
@RestController
@RequestMapping("/api/v1/members/me")
@RequiredArgsConstructor
public class MemberCommandController {

    private final MemberCommandService memberCommandService;
    private final MemberQueryService memberQueryService;
    private final AuthService authService;

    @Operation(
            summary = "내 프로필 수정",
            description = """
                    로그인한 사용자의 프로필 정보를 수정합니다.
                    - 닉네임, 한 줄 소개(Bio), 프로필 이미지 URL 등을 변경할 수 있습니다.
                    """
            ,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "프로필 수정 성공",
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
    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse<MemberProfileResponse>> updateMyProfile(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestBody UpdateMemberProfileRequest req
    ) {
        Long memberId = principal.getMemberId();
        MemberProfileResponse res = memberCommandService.updateProfile(memberId, req);
        return ResponseEntity.ok(ApiResponse.success(res));
    }


    @Operation(
            summary = "웹 푸시 허용 여부 수중",
            description = """
                    웹 푸시 허용 여부를 수정합니다.
                    """
            ,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @PatchMapping("/webpush-agree")
    public ApiResponse<Void> updateWebPushAgreement(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestBody WebPushAgreeRequest req
    ) {

        memberCommandService.updateWebPushAgreement(
                principal.getMemberId(),
                req.getAgreed()
        );

        return ApiResponse.success(null);
    }


    @Operation(
            summary = "회원 탈퇴",
            description = """
                    로그인한 사용자가 자신의 계정을 탈퇴합니다.
                    
                    - 이메일/비밀번호 기반(local) 계정:
                      현재 비밀번호를 검증한 뒤 탈퇴 처리합니다.
                    - 소셜 계정(kakao 등):
                      비밀번호 없이 탈퇴할 수 있으며, 소셜 계정 연결도 함께 해제됩니다.
                    
                    탈퇴 시 서버에 저장된 Refresh Token도 함께 삭제되며,
                    이후 모든 인증이 무효화됩니다.
                    """,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "회원 탈퇴 성공",
                    content = @Content(schema = @Schema(implementation = Void.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "비밀번호 미입력 등 잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패(비밀번호 불일치 등)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deactivateMember(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestBody(required = false) MemberDeactivateRequest req
    ) {
        Long memberId = principal.getMemberId();

        // 현재 회원 정보 조회
        Member member = memberQueryService.getMember(memberId);

        // 이미 탈퇴된 계정이면 그냥 멱등 처리
        if (member.isDeleted()) {
            return ResponseEntity.ok(ApiResponse.success(null));
        }

        // local 계정이면 비밀번호 검증
        if ("local".equalsIgnoreCase(member.getAuthProvider())) {
            if (req == null || req.getCurrentPassword() == null || req.getCurrentPassword().isBlank()) {
                throw new BusinessException(
                        ErrorCode.INVALID_PASSWORD
                );
            }

            authService.checkCurrentPassword(memberId, req.getCurrentPassword());
        }

        // 소셜 계정 unlink
        authService.unlinkSocialIfNeeded(member);

        // soft delete 처리
        memberCommandService.deactivateMember(memberId);

        // 로그아웃 처리
        authService.logout(memberId);

        return ResponseEntity.ok(ApiResponse.success(null));
    }
}