package com.opu.opube.feature.member.query.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.member.command.application.dto.response.MemberProfileResponse;
import com.opu.opube.feature.member.query.dto.response.MemberSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.opu.opube.feature.member.query.service.MemberQueryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members/me")
public class MemberQueryController {

    private final MemberQueryService memberQueryService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<MemberProfileResponse>> getMyProfile(
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        Long memberId = principal.getMemberId();
        MemberProfileResponse res = memberQueryService.getMyProfile(memberId);
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<MemberSummaryResponse>> getMySummary(
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        Long memberId = principal.getMemberId();
        MemberSummaryResponse response = memberQueryService.getMemberSummary(memberId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}