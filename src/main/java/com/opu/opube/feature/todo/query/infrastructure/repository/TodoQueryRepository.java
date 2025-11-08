package com.opu.opube.feature.todo.query.infrastructure.repository;

import java.util.List;
import com.opu.opube.feature.todo.command.domain.aggregate.Todo;

public interface TodoQueryRepository {
    List<Todo> findAll();
}