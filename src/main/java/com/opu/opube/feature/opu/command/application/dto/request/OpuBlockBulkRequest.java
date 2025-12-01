package com.opu.opube.feature.opu.command.application.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class OpuBlockBulkRequest {
    private List<Long> opuIds;
}