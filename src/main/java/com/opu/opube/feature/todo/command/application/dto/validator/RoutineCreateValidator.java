package com.opu.opube.feature.todo.command.application.dto.validator;

import com.opu.opube.feature.todo.command.application.dto.request.RoutineCreateDto;
import com.opu.opube.feature.todo.command.domain.aggregate.Frequency;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RoutineCreateValidator implements ConstraintValidator<RoutineCreateConstraint, RoutineCreateDto> {

    @Override
    public boolean isValid(RoutineCreateDto dto, ConstraintValidatorContext context) {
        if (dto == null) return true;

        Frequency freq = dto.getFrequency();

        switch (freq) {
            case WEEKLY, BIWEEKLY -> {
                if (isBlank(dto.getWeekDays())) {
                    addViolation(context, "weekDays 는 WEEKLY 또는 BIWEEKLY일 때 필수입니다.", "weekDays");
                    return false;
                }
            }

            case MONTHLY -> {
                if (isBlank(dto.getMonthDays())) {
                    addViolation(context, "monthDays 는 MONTHLY일 때 필수입니다.", "monthDays");
                    return false;
                }
            }

            case YEARLY -> {
                if (isBlank(dto.getYearDays())) {
                    addViolation(context, "yearDays 는 YEARLY일 때 필수입니다.", "yearDays");
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private void addViolation(ConstraintValidatorContext context, String message, String field) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(field)
                .addConstraintViolation();
    }
}
