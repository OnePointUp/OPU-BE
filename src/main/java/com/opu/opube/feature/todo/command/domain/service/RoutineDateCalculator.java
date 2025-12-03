package com.opu.opube.feature.todo.command.domain.service;


import com.opu.opube.feature.todo.command.domain.aggregate.Routine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.HashSet;
import java.util.Set;

import static com.opu.opube.feature.todo.command.domain.service.RoutineDateParser.*;

@Component
@RequiredArgsConstructor
public class RoutineDateCalculator {

    public Set<LocalDate> getDates(Routine routine) {
        return switch (routine.getFrequency()) {
            case DAILY -> daily(routine);
            case WEEKLY -> weekly(routine);
            case BIWEEKLY -> biWeekly(routine);
            case MONTHLY -> monthly(routine);
            case YEARLY -> yearly(routine);
            default -> throw new IllegalArgumentException("Unsupported frequency");
        };
    }

    private Set<LocalDate> daily(Routine routine) {
        Set<LocalDate> days = new HashSet<>();
        LocalDate date = routine.getStartDate();
        while (!date.isAfter(routine.getEndDate())) {
            days.add(date);
            date = date.plusDays(1);
        }
        return days;
    }

    private Set<LocalDate> weekly(Routine routine) {
        Set<Integer> daysOfWeek = parseWeekDays(routine.getWeekDays()); // 0~6
        Set<LocalDate> days = new HashSet<>();

        LocalDate date = routine.getStartDate();
        LocalDate end = routine.getEndDate();

        while (!date.isAfter(end)) {
            int dow = date.getDayOfWeek().getValue() % 7;
            if (daysOfWeek.contains(dow)) days.add(date);
            date = date.plusDays(1);
        }
        return days;
    }

    private int getSundayStartWeek(LocalDate date) {
        int dow = date.getDayOfWeek().getValue() % 7;
        LocalDate sunday = date.minusDays(dow);
        return sunday.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
    }

    private Set<LocalDate> biWeekly(Routine routine) {
        Set<Integer> targetWeekDays = parseWeekDays(routine.getWeekDays());
        Set<LocalDate> days = new HashSet<>();

        LocalDate start = routine.getStartDate();
        LocalDate end = routine.getEndDate();

        int baseWeekParity = getSundayStartWeek(start) % 2;

        LocalDate cur = start;
        while (!cur.isAfter(end)) {
            int curParity = getSundayStartWeek(cur) % 2;
            int dow = cur.getDayOfWeek().getValue() % 7;

            if (curParity == baseWeekParity && targetWeekDays.contains(dow)) {
                days.add(cur);
            }
            cur = cur.plusDays(1);
        }
        return days;
    }

    private Set<LocalDate> monthly(Routine routine) {
        Set<LocalDate> days = new HashSet<>();
        LocalDate date = routine.getStartDate();
        LocalDate end = routine.getEndDate();

        int currentMonth = -1;
        Set<Integer> monthDays = new HashSet<>();

        while (!date.isAfter(end)) {
            if (date.getMonthValue() != currentMonth) {
                monthDays = parseMonthDays(routine.getMonthDays(), date);
                currentMonth = date.getMonthValue();
            }
            if (monthDays.contains(date.getDayOfMonth())) {
                days.add(date);
            }
            date = date.plusDays(1);
        }
        return days;
    }

    private Set<LocalDate> yearly(Routine routine) {
        Set<LocalDate> yearDays = parseYearDays(
                routine.getDays(),
                routine.getStartDate().getYear(),
                routine.getEndDate().getYear()
        );

        Set<LocalDate> filtered = new HashSet<>();
        for (LocalDate d : yearDays) {
            if (!d.isBefore(routine.getStartDate()) && !d.isAfter(routine.getEndDate())) {
                filtered.add(d);
            }
        }
        return filtered;
    }
}