package com.opu.opube.feature.opu.command.application.service;

import com.opu.opube.feature.opu.command.application.dto.request.OpuRegisterDto;

public interface OpuCommandService {
    Long registerOpu(OpuRegisterDto dto, Long memberId);
    // command methods here
}