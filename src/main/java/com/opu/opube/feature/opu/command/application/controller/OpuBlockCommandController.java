package com.opu.opube.feature.opu.command.application.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.feature.opu.command.application.dto.request.OpuBlockBulkRequest;
import com.opu.opube.feature.opu.command.application.service.OpuBlockCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/opus")
public class OpuBlockCommandController {

    private final OpuBlockCommandService opuBlockCommandService;

    // 차단
    @PostMapping("/{opuId}/block")
    public ApiResponse<Void> blockOpu(
            @AuthenticationPrincipal(expression = "memberId") Long memberId,
            @PathVariable Long opuId
    ) {

        opuBlockCommandService.blockOpu(memberId, opuId);
        return ApiResponse.success(null);
    }

    // 차단 해제
    @DeleteMapping("/{opuId}/block")
    public ApiResponse<Void> unblockOpu(
            @AuthenticationPrincipal(expression = "memberId") Long memberId,
            @PathVariable Long opuId
    ) {

        opuBlockCommandService.unblockOpu(memberId, opuId);
        return ApiResponse.success(null);
    }

    // 다중 차단 해제
    @DeleteMapping("/blocks")
    public ApiResponse<Void> unblockOpuBulk(
            @AuthenticationPrincipal(expression = "memberId") Long memberId,
            @RequestBody OpuBlockBulkRequest request
    ) {
        opuBlockCommandService.unblockOpuBulk(memberId, request.getOpuIds());
        return ApiResponse.success(null);
    }
}