package com.opu.opube.feature.opu.command.application.dto.response;

public record OpuDuplicateItem(
        Long opuId,
        String title,
        Integer requiredMinutes,
        Long categoryId
) {}