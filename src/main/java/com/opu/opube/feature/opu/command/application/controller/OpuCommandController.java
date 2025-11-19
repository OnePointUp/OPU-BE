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
@RequestMapping("/api/v1/opu")
public class OpuCommandController {

    private final OpuCommandService opuCommandService;

    @PostMapping()
    public ResponseEntity<ApiResponse<Long>> createOpu(
            @AuthenticationPrincipal MemberPrincipal memberPrincipal,
            @RequestBody OpuRegisterDto dto
            ) {
        Long memberId = 1L; //memberPrincipal.getMemberId();
        Long opuId = opuCommandService.registerOpu(dto, memberId);

        return ResponseEntity.ok(ApiResponse.success(opuId));
    }
}