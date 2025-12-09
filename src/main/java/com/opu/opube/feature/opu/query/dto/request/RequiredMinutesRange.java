package com.opu.opube.feature.opu.query.dto.request;// feature.opu.query.dto

public enum RequiredMinutesRange {
    TOTAL("전체", null),
    MIN_1("1분", 1),
    MIN_5("5분", 5),
    MIN_30("30분", 30),
    HOUR_1("1시간", 60),
    DAY_1("1일", 1440);

    private final String label;
    private final Integer maxMinutes;

    RequiredMinutesRange(String label, Integer maxMinutes) {
        this.label = label;
        this.maxMinutes = maxMinutes;
    }

    public String label() {
        return label;
    }

    public Integer maxMinutes() {
        return maxMinutes;
    }
}