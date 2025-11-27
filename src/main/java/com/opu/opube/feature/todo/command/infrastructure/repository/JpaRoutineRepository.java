package com.opu.opube.feature.todo.command.infrastructure.repository;

import com.opu.opube.feature.todo.command.domain.aggregate.Routine;
import com.opu.opube.feature.todo.command.domain.repository.RoutineRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaRoutineRepository extends RoutineRepository, JpaRepository<Routine, Long> {
}