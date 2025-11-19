package com.opu.opube.feature.opu.command.application.dto.request;

import lombok.Getter;

@Getter
public class OpuRegisterDto {
    public String title;
    public String description;
    public String emoji;
    public Integer requiredMinutes;
    public Boolean isShared;
    public Long categoryId;
}
