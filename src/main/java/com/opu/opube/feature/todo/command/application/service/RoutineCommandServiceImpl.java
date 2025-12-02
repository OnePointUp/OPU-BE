package com.opu.opube.feature.todo.command.application.service;

import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.member.query.service.MemberQueryService;
import com.opu.opube.feature.todo.command.application.dto.request.RoutineCreateDto;
import com.opu.opube.feature.todo.command.domain.aggregate.Routine;
import com.opu.opube.feature.todo.command.domain.repository.RoutineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoutineCommandServiceImpl implements RoutineCommandService {
    private final RoutineRepository routineRepository;
    private final MemberQueryService memberQueryService;
    private final TodoCommandService todoCommandService;


    @Override
    @Transactional
    public Long createRoutine(Long memberId, RoutineCreateDto routineCreateDto) {
        Member member = memberQueryService.getMember(memberId);

        // routine
        Routine routine = Routine.toEntity(routineCreateDto, member);
        Routine savedRoutine = routineRepository.save(routine);

        // todo
        todoCommandService.createTodoByRoutine(member, savedRoutine);

        return savedRoutine.getId();
    }
}