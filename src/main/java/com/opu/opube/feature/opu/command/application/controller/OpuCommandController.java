package com.opu.opube.feature.opu.command.application.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.opu.command.application.dto.request.OpuRegisterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.opu.opube.feature.opu.command.application.service.OpuCommandService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/opus")
public class OpuCommandController {

    private final OpuCommandService opuCommandService;

    @PostMapping()
    public ResponseEntity<ApiResponse<Long>> createOpu(
            @AuthenticationPrincipal MemberPrincipal memberPrincipal,
            @RequestBody OpuRegisterDto dto
            ) {
        Long memberId = memberPrincipal.getMemberId();
        Long opuId = opuCommandService.registerOpu(dto, memberId);

        return ResponseEntity.ok(ApiResponse.success(opuId));
    }


    // 공개 처리
    @PatchMapping("/{opuId}/share")
    public ResponseEntity<ApiResponse<Void>> shareOpu(
            @AuthenticationPrincipal(expression = "memberId") Long memberId,
            @PathVariable Long opuId
    ) {
        opuCommandService.shareOpu(memberId, opuId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 비공개 처리
    @PatchMapping("/{opuId}/unshare")
    public ResponseEntity<ApiResponse<Void>> unshareOpu(
            @AuthenticationPrincipal(expression = "memberId") Long memberId,
            @PathVariable Long opuId
    ) {
        opuCommandService.unshareOpu(memberId, opuId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{opuId}")
    public ResponseEntity<ApiResponse<Void>> deleteOpu(
            @AuthenticationPrincipal(expression = "memberId") Long memberId,
            @PathVariable Long opuId
    ) {
        opuCommandService.deleteOpu(memberId, opuId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}