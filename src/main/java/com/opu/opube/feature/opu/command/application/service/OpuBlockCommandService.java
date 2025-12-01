package com.opu.opube.feature.opu.command.application.service;

import java.util.List;

public interface OpuBlockCommandService {

    void blockOpu(Long memberId, Long opuId);

    void unblockOpu(Long memberId, Long opuId);

    void unblockOpuBulk(Long memberId, List<Long> opuIds);
}