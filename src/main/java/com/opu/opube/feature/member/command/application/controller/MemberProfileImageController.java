package com.opu.opube.feature.member.command.application.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.common.s3.dto.PresignedUrlResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.member.command.application.service.MemberProfileImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members/me")
@RequiredArgsConstructor
public class MemberProfileImageController {

    private final MemberProfileImageService memberProfileImageService;

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