package com.opu.opube.feature.todo.command.domain.repository;

import java.util.UUID;
import com.opu.opube.feature.todo.command.domain.aggregate.Todo;

public interface TodoRepository {
    Todo save(Todo todo);
}