package com.opu.opube.feature.todo.command.application.dto.validator;

import com.opu.opube.feature.todo.command.application.dto.request.RoutineCreateDto;
import com.opu.opube.feature.todo.command.application.dto.request.RoutineUpdateDto;
import com.opu.opube.feature.todo.command.application.dto.request.RoutineUpdateScope;
import com.opu.opube.feature.todo.command.domain.aggregate.Frequency;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RoutineUpdateValidator implements ConstraintValidator<RoutineUpdateConstraint, RoutineUpdateDto> {

    @Override
    public boolean isValid(RoutineUpdateDto dto, ConstraintValidatorContext context) {
        if (dto == null) return true;

        // title, color, frequency 셋 중 적어도 하나는 null이 아니어야 함
        if (dto.getTitle() == null && dto.getColor() == null && dto.getFrequency() == null) {
            addViolation(context, "title, color, frequency 중 적어도 하나는 지정해야 합니다.", null);
            return false;
        }

        // routine에 freq가 존재하면, RoutineUpdateScope도 존재해야 함
        if (dto.getFrequency() != null && dto.getScope() == null) {
            addViolation(context, "frequency 변경 시 scope는 필수입니다.", "scope");
            return false;
        }

        Frequency freq = dto.getFrequency();

        if (freq == null) {
            return true;
        }

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
                if (isBlank(dto.getDays())) {
                    addViolation(context, "days 는 YEARLY일 때 필수입니다.", "yearDays");
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
