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

    @Override
    @Transactional
    public void updateRoutine(Long memberId, RoutineUpdateDto dto, Long routineId) {
        Member member = memberQueryService.getMember(memberId);
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROUTINE_NOT_FOUND));

        if (!routine.getMember().getId().equals(member.getId())) {
            throw new BusinessException(ErrorCode.ROUTINE_FORBIDDEN);
        }

        // routine 업데이트
        routine.update(dto);

        // todos 업데이트
        todoCommandService.updateTodoByRoutine(routine.getId(), dto.getTitle(), dto.getAlarmTime());

        // 변경된 루틴 주기 기반으로 Todos diff 반영
        todoCommandService.updateTodoByRoutineChange(member, routine, dto.getScope());
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