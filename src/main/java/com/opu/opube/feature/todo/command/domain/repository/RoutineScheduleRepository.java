package com.opu.opube.feature.todo.command.domain.repository;

import com.opu.opube.feature.todo.command.domain.aggregate.RoutineSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoutineScheduleRepository extends JpaRepository<RoutineSchedule, Long> {
}