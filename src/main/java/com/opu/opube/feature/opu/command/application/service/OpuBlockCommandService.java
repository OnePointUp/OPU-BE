package com.opu.opube.feature.opu.command.application.service;

public interface OpuBlockCommandService {

    void blockOpu(Long memberId, Long opuId);

    void unblockOpu(Long memberId, Long opuId);
}