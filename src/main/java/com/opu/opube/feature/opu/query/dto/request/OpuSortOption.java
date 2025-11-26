package com.opu.opube.feature.opu.query.dto.request;

import lombok.Getter;

@Getter
public enum OpuSortOption {
    NEWEST("newest"),
    NAME_ASC("name_asc"),
    COMPLETION("completion"),
    FAVORITE("favorite");

    private final String code;

    OpuSortOption(String code) {
        this.code = code;
    }

    public static OpuSortOption from(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }

        for (OpuSortOption option : values()) {
            if (option.code.equalsIgnoreCase(code)) {
                return option;
            }
        }

        return null;
    }
}