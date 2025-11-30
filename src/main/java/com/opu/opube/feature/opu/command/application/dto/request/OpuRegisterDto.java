package com.opu.opube.feature.opu.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class OpuRegisterDto {

    @NotBlank(message = "OPU 제목은 비어 있을 수 없습니다.")
    @Size(min = 2, max = 30, message = "OPU 제목은 2자 이상 30자 이하로 입력해야 합니다.")
    private String title;

    @Size(max = 100, message = "설명은 100자 이하로 입력해야 합니다.")
    private String description;

    @NotBlank(message = "이모지는 비어 있을 수 없습니다.")
    private String emoji;

    @NotNull(message = "필요 시간(requiredMinutes)은 필수입니다.")
    private Integer requiredMinutes;

    @NotNull(message = "공유 여부(isShared)는 필수입니다.")
    private Boolean isShared;

    @NotNull(message = "카테고리 ID는 필수입니다.")
    private Long categoryId;
}