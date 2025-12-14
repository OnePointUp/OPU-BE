package com.opu.opube.feature.opu.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(
        description = """
                OPU 생성/공개 처리 결과 응답
                - created=true  : OPU 생성(또는 공개) 성공
                - created=false : 유사한 공개 OPU가 존재하여 생성되지 않음
                """
)
public record OpuRegisterResponse(

        @Schema(
                description = "OPU 생성 또는 공개 성공 여부",
                example = "true"
        )
        boolean created,

        @Schema(
                description = "생성된 OPU ID",
                example = "123"
        )
        Long opuId,

        @Schema(
                description = "유사한 공개 OPU 목록"
        )
        List<OpuDuplicateItem> duplicates
) {
    public static OpuRegisterResponse created(Long id) {
        return new OpuRegisterResponse(true, id, List.of());
    }

    public static OpuRegisterResponse duplicated(List<OpuDuplicateItem> duplicates) {
        return new OpuRegisterResponse(false, null, duplicates);
    }
}