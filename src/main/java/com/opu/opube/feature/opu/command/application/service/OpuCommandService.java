package com.opu.opube.feature.opu.command.application.service;

import com.opu.opube.feature.opu.command.application.dto.request.OpuRegisterDto;
import com.opu.opube.feature.opu.command.application.dto.response.OpuRegisterResponse;

public interface OpuCommandService {
    OpuRegisterResponse registerOpu(OpuRegisterDto dto, Long memberId);

    OpuRegisterResponse shareOpu(Long memberId, Long opuId);

    void unshareOpu(Long memberId, Long opuId);

    void deleteOpu(Long memberId, Long opuId);
}