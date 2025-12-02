package com.opu.opube.feature.opu.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class OpuRegisterDto {

    @Schema(
            description = "OPU 제목",
            example = "물 1컵 마시기",
            minLength = 2,
            maxLength = 30
    )
    @NotBlank(message = "OPU 제목은 비어 있을 수 없습니다.")
    @Size(min = 2, max = 30, message = "OPU 제목은 2자 이상 30자 이하로 입력해야 합니다.")
    private String title;

    @Schema(
            description = "OPU 설명",
            example = "건강한 하루를 위해 지금 바로 물 한 잔!",
            maxLength = 100
    )
    @Size(max = 100, message = "설명은 100자 이하로 입력해야 합니다.")
    private String description;

    @Schema(
            description = "OPU 대표 이모지",
            example = "💧"
    )
    @NotBlank(message = "이모지는 비어 있을 수 없습니다.")
    private String emoji;

    @Schema(
            description = "OPU 수행에 필요한 시간(분)",
            example = "5"
    )
    @NotNull(message = "필요 시간은 필수입니다.")
    private Integer requiredMinutes;

    @Schema(
            description = "OPU 공개 여부",
            example = "true"
    )
    @NotNull(message = "공유 여부는 필수입니다.")
    private Boolean isShared;

    @Schema(
            description = "OPU 카테고리 ID",
            example = "3"
    )
    @NotNull(message = "카테고리 ID는 필수입니다.")
    private Long categoryId;
}