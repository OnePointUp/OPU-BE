package com.opu.opube.feature.todo.query.infrastructure.repository;

import com.opu.opube.common.dto.PageResponse;
import com.opu.opube.feature.todo.command.domain.aggregate.QTodo;
import com.opu.opube.feature.todo.query.dto.response.TodoResponseDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TodoQueryRepositoryImpl implements TodoQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 사용자 기준, 날짜별 Todo 조회
     */
    @Override
    public PageResponse<TodoResponseDto> getTodoByUserAndDate(Long memberId, LocalDate date, int page, int size) {
        QTodo todo = QTodo.todo;

        Long total = queryFactory
                .select(todo.count())
                .from(todo)
                .where(todo.member.id.eq(memberId)
                        .and(todo.scheduledDate.eq(date)))
                .fetchOne();

        long totalCount = total != null ? total : 0L;

        List<TodoResponseDto> content = queryFactory
                .selectFrom(todo)
                .where(todo.member.id.eq(memberId)
                        .and(todo.scheduledDate.eq(date)))
                .orderBy(todo.sortOrder.asc())
                .offset((long) page * size)
                .limit(size)
                .fetch()
                .stream()
                .map(TodoResponseDto::fromEntity)
                .collect(Collectors.toList());

        return PageResponse.from(content, totalCount, page, size);
    }
}