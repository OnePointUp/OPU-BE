package com.opu.opube.feature.todo.command.domain.repository;

import com.opu.opube.feature.todo.command.domain.aggregate.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
}