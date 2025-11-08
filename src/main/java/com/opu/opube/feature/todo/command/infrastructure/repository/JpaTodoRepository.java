package com.opu.opube.feature.todo.command.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.opu.opube.feature.todo.command.domain.aggregate.Todo;
import com.opu.opube.feature.todo.command.domain.repository.TodoRepository;

public interface JpaTodoRepository extends TodoRepository, JpaRepository<Todo, Long> {
}