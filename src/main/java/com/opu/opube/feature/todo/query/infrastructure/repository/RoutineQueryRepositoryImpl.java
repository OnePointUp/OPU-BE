package com.opu.opube.feature.todo.query.infrastructure.repository;

import com.opu.opube.common.dto.PageResponse;
import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.todo.command.domain.aggregate.QRoutine;
import com.opu.opube.feature.todo.command.domain.aggregate.QTodo;
import com.opu.opube.feature.todo.command.domain.aggregate.Routine;
import com.opu.opube.feature.todo.query.dto.response.*;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RoutineQueryRepositoryImpl implements RoutineQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RoutineListResponseDto> getRoutines(Long memberId, int page, int size) {
        QRoutine routine = QRoutine.routine;

        Long total = queryFactory
                .select(routine.count())
                .from(routine)
                .where(routine.member.id.eq(memberId))
                .fetchOne();

        long totalCount = total != null ? total : 0L;

        List<RoutineListResponseDto> content = queryFactory
                .selectFrom(routine)
                .where(routine.member.id.eq(memberId))
                .orderBy(routine.createdAt.asc())
                .offset((long) page * size)
                .limit(size)
                .fetch()
                .stream()
                .map(RoutineListResponseDto::fromEntity)
                .collect(Collectors.toList());

        return PageResponse.from(content, totalCount, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public RoutineDetailResponseDto getRoutine(Long memberId, Long routineId) {
        QRoutine routine = QRoutine.routine;

        Routine entity = queryFactory
                .selectFrom(routine)
                .where(routine.member.id.eq(memberId)
                        .and(routine.id.eq(routineId)))
                .fetchOne();

        if (entity == null) {
            throw new BusinessException(ErrorCode.ROUTINE_NOT_FOUND);
        }

        return RoutineDetailResponseDto.fromEntity(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RoutineSummaryResponseDto> getRoutineTitle(Long memberId, int page, int size) {
        QRoutine routine = QRoutine.routine;

        Long total = queryFactory
                .select(routine.count())
                .from(routine)
                .where(routine.member.id.eq(memberId))
                .fetchOne();

        long totalCount = total != null ? total : 0L;

        List<RoutineSummaryResponseDto> content = queryFactory
                .select(Projections.constructor(
                        RoutineSummaryResponseDto.class,
                        routine.title.as("title"),
                        routine.id.as("id")
                ))
                .from(routine)
                .where(routine.member.id.eq(memberId))
                // todo : todo 많이 생성된 순으로 정렬
                .offset((long) page * size)
                .limit(size)
                .fetch();
        return PageResponse.from(content, totalCount, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RoutineStatResponseDto> getRoutineStatList(Long memberId, int page, int size) {
        QRoutine routine = QRoutine.routine;

        Long total = queryFactory
                .select(routine.count())
                .from(routine)
                .where(routine.member.id.eq(memberId))
                .fetchOne();

        long totalCount = total != null ? total : 0L;

        List<RoutineStatResponseDto> content = queryFactory
                .select(Projections.constructor(
                        RoutineStatResponseDto.class,
                        routine.title.as("title"),
                        routine.id.as("id"),
                        routine.color.as("color")
                ))
                .from(routine)
                .where(routine.member.id.eq(memberId))
                .offset((long) page * size)
                .limit(size)
                .fetch();
        return PageResponse.from(content, totalCount, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public MonthlyRoutineStats getMonthlyRoutineStats(
            Long memberId, Long routineId, LocalDate startDate, LocalDate endDate
    ) {
        QTodo todo = QTodo.todo;

        List<TodoStatRow> dailyStats = queryFactory
                .select(
                        Projections.constructor(
                                TodoStatRow.class,
                                todo.routine.id,
                                todo.scheduledDate,
                                todo.id.count().gt(0L),
                                todo.completed.when(true).then(1L).otherwise(0L).sum().gt(0L)
                        )
                )
                .from(todo)
                .where(
                        todo.member.id.eq(memberId),
                        todo.routine.id.eq(routineId),
                        todo.deletedAt.isNull(),
                        todo.scheduledDate.between(startDate, endDate)
                )
                .groupBy(todo.routine.id, todo.scheduledDate)
                .fetch();

        long scheduledCount = dailyStats.size();
        long completedCount = dailyStats.stream()
                .filter(row -> Boolean.TRUE.equals(row.getDone()))
                .count();

        return new MonthlyRoutineStats(scheduledCount, completedCount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TodoStatRow> findDailyCompletion(
            Long memberId, Long routineId, LocalDate startDate, LocalDate endDate
    ) {
        QTodo todo = QTodo.todo;

        return queryFactory
                .select(
                        Projections.constructor(
                                TodoStatRow.class,
                                todo.routine.id,
                                todo.scheduledDate,
                                todo.id.count().gt(0L),
                                todo.completed.when(true).then(1L).otherwise(0L).sum().gt(0L)
                        )
                )
                .from(todo)
                .where(
                        todo.member.id.eq(memberId),
                        todo.routine.id.eq(routineId),
                        todo.deletedAt.isNull(),
                        todo.scheduledDate.between(startDate, endDate)
                )
                .groupBy(todo.routine.id, todo.scheduledDate)
                .orderBy(todo.scheduledDate.desc())
                .fetch();
    }
}