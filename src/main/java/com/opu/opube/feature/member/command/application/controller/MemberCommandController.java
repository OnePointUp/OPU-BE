package com.opu.opube.feature.member.command.application.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.member.command.application.dto.request.UpdateMemberProfileRequest;
import com.opu.opube.feature.member.command.application.dto.response.MemberProfileResponse;
import com.opu.opube.feature.member.command.application.service.MemberCommandService;
import com.opu.opube.feature.member.query.service.MemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members/me")
@RequiredArgsConstructor
public class MemberCommandController {

    private final MemberCommandService memberCommandService;
    private final MemberQueryService memberQueryService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<MemberProfileResponse>> getMyProfile(
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        Long memberId = principal.getMemberId();
        MemberProfileResponse res = memberQueryService.getMyProfile(memberId);
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse<MemberProfileResponse>> updateMyProfile(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestBody UpdateMemberProfileRequest req
    ) {
        Long memberId = principal.getMemberId();
        MemberProfileResponse res = memberCommandService.updateProfile(memberId, req);
        return ResponseEntity.ok(ApiResponse.success(res));
    }
}