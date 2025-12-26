package com.opu.opube.feature.todo.command.application.service;

import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.member.query.service.MemberQueryService;
import com.opu.opube.feature.todo.command.application.dto.request.RoutineCreateDto;
import com.opu.opube.feature.todo.command.application.dto.request.RoutineScope;
import com.opu.opube.feature.todo.command.application.dto.request.RoutineUpdateDto;
import com.opu.opube.feature.todo.command.domain.aggregate.Routine;
import com.opu.opube.feature.todo.command.domain.repository.RoutineRepository;
import com.opu.opube.feature.todo.command.domain.service.RoutineDateCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class RoutineCommandServiceImpl implements RoutineCommandService {
    private final RoutineRepository routineRepository;
    private final MemberQueryService memberQueryService;
    private final TodoCommandService todoCommandService;
    private final RoutineDateCalculator routineDateCalculator;

    @Override
    @Transactional
    public Long createRoutine(Long memberId, RoutineCreateDto routineCreateDto) {
        Member member = memberQueryService.getMember(memberId);

        // routine
        Routine routine = Routine.toEntity(routineCreateDto, member);

        Set<LocalDate> dates = routineDateCalculator.getDates(routine);
        if (dates.isEmpty()) {
            throw new BusinessException(ErrorCode.ROUTINE_NO_TODO_DATES);
        }

        Routine savedRoutine = routineRepository.save(routine);

        // todo
        todoCommandService.createTodoByRoutine(member, savedRoutine, dates);

        return savedRoutine.getId();
    }

    @Override
    @Transactional
    public void updateRoutine(Long memberId, RoutineUpdateDto dto, Long routineId) {
        Member member = memberQueryService.getMember(memberId);
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROUTINE_NOT_FOUND));

        if (!routine.getMember().getId().equals(member.getId())) {
            throw new BusinessException(ErrorCode.ROUTINE_FORBIDDEN);
        }

        routine.update(dto);

        todoCommandService.updateTodoByRoutineChange(member, routine, dto.getScope());

        todoCommandService.updateTodoByRoutine(routine.getId(), dto.getTitle(), dto.getAlarmTime());
    }

    @Override
    @Transactional
    public void deleteRoutine(Long memberId, Long routineId, RoutineScope scope) {
        Member member = memberQueryService.getMember(memberId);
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROUTINE_NOT_FOUND));

        if (!routine.getMember().getId().equals(member.getId())) {
            throw new BusinessException(ErrorCode.ROUTINE_FORBIDDEN);
        }

        todoCommandService.deleteTodoByRoutine(routine, scope);
        routineRepository.deleteById(routineId);
    }
}