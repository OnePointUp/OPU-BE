package com.opu.opube.feature.todo.command.domain.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class RoutineDateParser {

    // weekDays 문자열 -> Set<Integer> 변환
    public static Set<Integer> parseWeekDays(String weekDays) {
        if (weekDays == null || weekDays.isBlank()) return Collections.emptySet();
        return Arrays.stream(weekDays.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
    }

    // monthDays 문자열 -> Set<Integer> 변환
    public static Set<Integer> parseMonthDays(String monthDays, LocalDate start) {
        if (monthDays == null || monthDays.isBlank()) return Collections.emptySet();
        Set<Integer> result = new HashSet<>();
        for (String s : monthDays.split(",")) {
            s = s.trim();
            if ("L".equalsIgnoreCase(s)) {
                result.add(start.lengthOfMonth());
            } else {
                result.add(Integer.parseInt(s));
            }
        }
        return result;
    }

    // yearDays 문자열 -> Set<LocalDate> 변환
    public static Set<LocalDate> parseYearDays(String days, int startYear, int endYear) {
        Set<LocalDate> result = new HashSet<>();
        if (days == null || days.isBlank()) return result;

        for (String range : days.split(",")) {
            String[] parts = range.split("-");
            if (parts.length == 2) {
                int month = Integer.parseInt(parts[0]);
                int day = Integer.parseInt(parts[1]);
                for (int year = startYear; year <= endYear; year++) {
                    if (day <= YearMonth.of(year, month).lengthOfMonth()) {
                        result.add(LocalDate.of(year, month, day));
                    }
                }
            }
        }
        return result;
    }
}
