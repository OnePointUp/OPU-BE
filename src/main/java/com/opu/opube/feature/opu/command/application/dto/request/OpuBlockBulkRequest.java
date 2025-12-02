package com.opu.opube.feature.opu.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(
        description = "여러 OPU에 대한 차단 해제를 위한 요청 DTO"
)
public class OpuBlockBulkRequest {

    @Schema(
            description = "차단 해제할 OPU ID 리스트",
            example = "[3, 7, 12]"
    )
    private List<Long> opuIds;
}