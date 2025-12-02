package com.opu.opube.feature.todo.query.infrastructure.repository;

import com.opu.opube.common.dto.PageResponse;
import com.opu.opube.feature.todo.command.domain.aggregate.QRoutine;
import com.opu.opube.feature.todo.query.dto.response.RoutineListResponseDto;
import com.opu.opube.feature.todo.query.dto.response.TodoResponseDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RoutineQueryRepositoryImpl implements RoutineQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
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
}