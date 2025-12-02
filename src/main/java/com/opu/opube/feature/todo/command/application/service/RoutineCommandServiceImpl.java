package com.opu.opube.feature.todo.command.application.service;

import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.member.query.service.MemberQueryService;
import com.opu.opube.feature.todo.command.application.dto.request.RoutineCreateDto;
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
    public void updateRoutine(Long memberId, RoutineUpdateDto dto, Long routineId) {
        Member member = memberQueryService.getMember(memberId);
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROUTINE_NOT_FOUND));

        if (!routine.getMember().getId().equals(member.getId())) {
            throw new BusinessException(ErrorCode.ROUTINE_FORBIDDEN);
        }

        // routine 업데이트
        routine.update(dto);

        // todos 업데이트 - 반복 수정 x
        todoCommandService.updateTodoByRoutine(routine.getId(), dto.getTitle(), dto.getAlarmTime());

        // todos 업데이트 - 반복 수정 o
        // 기존 todos 삭제 처리
        switch (dto.getScope()) {
            case UNCOMPLETED_TODO -> todoCommandService.deleteUncompletedTodoByRoutine(routine.getId());
            case ALL -> todoCommandService.deleteTodoByRoutine(routine.getId());
        }

        // 기존 todos 연결 해제
        todoCommandService.unlinkToRoutine(routine.getId());

        // 업데이트 된 routine 기준으로 todos 새로 생성
        todoCommandService.createTodoByRoutine(member, routine);
    }
}