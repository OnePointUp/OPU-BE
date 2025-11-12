package com.opu.opube.feature.todo.command.domain.repository;

import com.opu.opube.feature.todo.command.domain.aggregate.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {
}