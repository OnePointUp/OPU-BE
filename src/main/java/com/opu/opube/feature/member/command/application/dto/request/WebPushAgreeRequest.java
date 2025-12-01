package com.opu.opube.feature.member.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class WebPushAgreeRequest {

    @Schema(
            description = "허용 여부",
            example = "true"
    )
    private Boolean agreed;
}