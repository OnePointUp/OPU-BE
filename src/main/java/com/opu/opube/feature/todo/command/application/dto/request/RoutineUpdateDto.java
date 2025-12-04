package com.opu.opube.feature.todo.command.application.dto.request;

import com.opu.opube.feature.todo.command.application.dto.validator.RoutineUpdateConstraint;
import com.opu.opube.feature.todo.command.domain.aggregate.Frequency;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@RoutineUpdateConstraint
@Schema(description = "Routine 수정 요청 DTO")
public class RoutineUpdateDto {
    @Schema(description = "routine 이름", example = "운동하기", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String title;
    @Schema(description = "색상 - HEX 색상", example = "#000000", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String color;
    @Schema(description = "예정 시간", example = "24:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    LocalTime alarmTime;
    @Schema(description = "종료 날짜", example = "2026-12-12", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    LocalDate endDate;
    @Schema(description = "반복 주기", example = "WEEKLY", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    Frequency frequency;
    @Schema(description = "frequency가 WEEKLY 일 시 필수", example = "0,1,2,3,4,5,6", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String weekDays;
    @Schema(description = "frequency가 MONTHLY 일 시 필수", example = "1,10,31,L", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String monthDays;
    @Schema(description = "frequency가 YEARLY 일 시 필수", example = "1-10,2-20", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String days;
    @Schema(description = "바뀐 할 일에 포함되지 않는 todo 삭제 정책 - 반복, 종료날짜가 바뀔 시 필수", example = "ALL", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    RoutineScope scope;
}
