package com.opu.opube.feature.member.command.application.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.common.s3.dto.PresignedUrlResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.member.command.application.service.MemberProfileImageService;
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
        name = "Member - Profile Image",
        description = "회원 프로필 이미지 업로드(Presigned URL) 관련 API"
)
@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class MemberProfileImageController {

    private final MemberProfileImageService memberProfileImageService;

    @Operation(
            summary = "프로필 이미지 업로드용 Presigned URL 발급",
            description = """
                    프로필 이미지를 S3에 업로드하기 위한 Presigned URL을 발급합니다.
                    """,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Presigned URL 발급 성공",
                    content = @Content(schema = @Schema(implementation = PresignedUrlResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/profile-image/presign")
    public ApiResponse<PresignedUrlResponse> generateProfileImagePresignedUrl(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestParam(required = false) String extension
    ) {
        Long memberId = principal.getMemberId();
        PresignedUrlResponse res = memberProfileImageService.createPresignedUrl(memberId, extension);
        return ApiResponse.success(res);
    }
}