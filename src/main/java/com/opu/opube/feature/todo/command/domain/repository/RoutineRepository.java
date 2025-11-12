package com.opu.opube.feature.todo.command.domain.repository;

import com.opu.opube.feature.todo.command.domain.aggregate.Routine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoutineRepository extends JpaRepository<Routine, Long> {
}