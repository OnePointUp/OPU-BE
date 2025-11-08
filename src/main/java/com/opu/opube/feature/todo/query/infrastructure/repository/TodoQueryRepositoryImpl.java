package com.opu.opube.feature.todo.query.infrastructure.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import com.opu.opube.feature.todo.command.domain.aggregate.Todo;
import com.opu.opube.feature.todo.command.domain.aggregate.QTodo;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TodoQueryRepositoryImpl implements TodoQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Todo> findAll() {
        QTodo todo = QTodo.todo;
        return queryFactory
                .selectFrom(todo)
                .fetch();
    }
}