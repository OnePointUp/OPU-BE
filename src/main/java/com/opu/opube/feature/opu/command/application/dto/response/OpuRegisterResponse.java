package com.opu.opube.feature.opu.command.application.dto.response;

import java.util.List;

public record OpuRegisterResponse(
        boolean created,     // true면 생성됨
        Long opuId,          // created=true일 때만 값 있음
        List<OpuDuplicateItem> duplicates // created=false일 때 유사 목록
) {
    public static OpuRegisterResponse created(Long id) {
        return new OpuRegisterResponse(true, id, List.of());
    }

    public static OpuRegisterResponse duplicated(List<OpuDuplicateItem> duplicates) {
        return new OpuRegisterResponse(false, null, duplicates);
    }
}