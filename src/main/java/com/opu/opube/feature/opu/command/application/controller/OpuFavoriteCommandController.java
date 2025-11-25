package com.opu.opube.feature.opu.command.application.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.opu.command.application.service.OpuFavoriteCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/opus")
@RequiredArgsConstructor
public class OpuFavoriteCommandController {

    private final OpuFavoriteCommandService opuFavoriteCommandService;

    @PostMapping("/{opuId}/favorite")
    public ApiResponse<Void> favorite(
            @PathVariable Long opuId,
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        Long memberId = principal.getMemberId();
        opuFavoriteCommandService.addFavorite(memberId, opuId);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{opuId}/favorite")
    public ApiResponse<Void> unfavorite(
            @PathVariable Long opuId,
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        Long memberId = principal.getMemberId();
        opuFavoriteCommandService.removeFavorite(memberId, opuId);
        return ApiResponse.success(null);
    }
}