package com.opu.opube.feature.opu.query.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.common.dto.PageResponse;
import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.opu.query.dto.request.OpuListFilterRequest;
import com.opu.opube.feature.opu.query.dto.response.OpuSummaryResponse;
import com.opu.opube.feature.opu.query.service.OpuQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/opus")
public class OpuQueryController {

    private final OpuQueryService opuQueryService;

    @GetMapping
    public ApiResponse<PageResponse<OpuSummaryResponse>> getSharedOpus(
            @AuthenticationPrincipal MemberPrincipal principal,
            @ModelAttribute OpuListFilterRequest filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Long loginMemberId = (principal != null) ? principal.getMemberId() : null;

        PageResponse<OpuSummaryResponse> result =
                opuQueryService.getOpuList(loginMemberId, filter, page, size);

        return ApiResponse.success(result);
    }

    @GetMapping("/my")
    public ApiResponse<PageResponse<OpuSummaryResponse>> getMyOpuList(
            @AuthenticationPrincipal MemberPrincipal principal,
            @ModelAttribute OpuListFilterRequest filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Long loginMemberId = principal.getMemberId();

        PageResponse<OpuSummaryResponse> result =
                opuQueryService.getMyOpuList(loginMemberId, filter, page, size);

        return ApiResponse.success(result);
    }
}