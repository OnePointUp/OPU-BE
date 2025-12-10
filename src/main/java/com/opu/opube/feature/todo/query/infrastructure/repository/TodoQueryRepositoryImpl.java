package com.opu.opube.feature.todo.query.infrastructure.repository;

import com.opu.opube.common.dto.PageResponse;
import com.opu.opube.feature.todo.command.domain.aggregate.QTodo;
import com.opu.opube.feature.todo.query.dto.response.DayTodoStats;
import com.opu.opube.feature.todo.query.dto.response.TodoResponseDto;
import com.opu.opube.feature.todo.query.dto.response.TodoStatRow;
import com.opu.opube.feature.todo.query.dto.response.TodoStatisticsDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.querydsl.core.types.dsl.Expressions.constant;

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
                        .and(todo.scheduledDate.eq(date))
                        .and(todo.deletedAt.isNull()))
                .fetchOne();

        long totalCount = total != null ? total : 0L;

        List<TodoResponseDto> content = queryFactory
                .selectFrom(todo)
                .where(todo.member.id.eq(memberId)
                        .and(todo.scheduledDate.eq(date))
                        .and(todo.deletedAt.isNull()))
                .orderBy(todo.sortOrder.asc())
                .offset((long) page * size)
                .limit(size)
                .fetch()
                .stream()
                .map(TodoResponseDto::fromEntity)
                .collect(Collectors.toList());

        return PageResponse.from(content, totalCount, page, size);
    }

    @Override
    public List<TodoStatisticsDto> findStatisticsByDateRange(Long memberId, LocalDate startDate, LocalDate endDate) {
        QTodo todo = QTodo.todo;

        // 완료된 Todos 를 조건부로 계산
        NumberExpression<Long> completedCount = Expressions.cases()
                .when(todo.completed.eq(true))
                .then(1L)
                .otherwise(0L)
                .sum();

        // 날짜별 통계 조회
        return queryFactory
                .select(Projections.constructor(
                        TodoStatisticsDto.class,
                        todo.scheduledDate.as("date"),
                        todo.count().as("totalCount"),                                           // 전체 Todo 수
                        completedCount.as("completedCount")
                ))
                .from(todo)
                .where(todo.member.id.eq(memberId)
                        .and(todo.scheduledDate.between(startDate, endDate))
                        .and(todo.deletedAt.isNull()))
                .groupBy(todo.scheduledDate)
                .orderBy(todo.scheduledDate.asc())
                .fetch();
    }

    @Override
    public List<DayTodoStats> getRoutineTodo(Long memberId, Long routineId, LocalDate start, LocalDate end) {
        QTodo todo = QTodo.todo;

        return queryFactory
                .select(Projections.constructor(
                        DayTodoStats.class,
                        todo.scheduledDate,
                        constant(true),
                        todo.completed

                ))
                .from(todo)
                .where(todo.member.id.eq(memberId)
                        .and(todo.routine.id.eq(routineId))
                        .and(todo.scheduledDate.goe(start))
                        .and(todo.scheduledDate.lt(end)))
                .orderBy(todo.scheduledDate.asc())
                .fetch();
    }

    @Override
    public List<TodoStatRow> getAllRoutineTodo(Long memberId, List<Long> routineIds, LocalDate start, LocalDate end) {
        QTodo todo = QTodo.todo;

        return queryFactory
                .select(Projections.constructor(
                        TodoStatRow.class,
                        todo.routine.id,
                        todo.scheduledDate,
                        constant(true),
                        todo.completed

                ))
                .from(todo)
                .where(todo.member.id.eq(memberId)
                        .and(todo.routine.id.in(routineIds))
                        .and(todo.scheduledDate.goe(start))
                        .and(todo.scheduledDate.lt(end)))
                .orderBy(todo.routine.id.asc(),
                        todo.scheduledDate.asc())
                .fetch();
    }
}