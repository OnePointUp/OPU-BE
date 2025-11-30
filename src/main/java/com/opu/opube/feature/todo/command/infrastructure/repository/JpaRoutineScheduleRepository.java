package com.opu.opube.feature.todo.command.infrastructure.repository;

import com.opu.opube.feature.todo.command.domain.aggregate.RoutineSchedule;
import com.opu.opube.feature.todo.command.domain.repository.RoutineScheduleRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaRoutineScheduleRepository extends RoutineScheduleRepository, JpaRepository<RoutineSchedule, Long> {
}