package com.opu.opube.feature.todo.command.application.service;

import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.member.query.service.MemberQueryService;
import com.opu.opube.feature.opu.command.application.service.MemberOpuCounterService;
import com.opu.opube.feature.opu.command.application.service.MemberOpuEventService;
import com.opu.opube.feature.opu.command.domain.aggregate.Opu;
import com.opu.opube.feature.opu.query.service.OpuQueryService;
import com.opu.opube.feature.todo.command.application.dto.request.*;
import com.opu.opube.feature.todo.command.domain.aggregate.Routine;
import com.opu.opube.feature.todo.command.domain.aggregate.Todo;
import com.opu.opube.feature.todo.command.domain.repository.TodoRepository;
import com.opu.opube.feature.todo.command.domain.service.RoutineDateCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoCommandServiceImpl implements TodoCommandService {

    private final TodoRepository todoRepository;
    private final MemberQueryService memberQueryService;
    private final MemberOpuCounterService memberOpuCounterService;
    private final MemberOpuEventService memberOpuEventService;
    private final OpuQueryService opuQueryService;
    private final RoutineDateCalculator routineDateCalculator;

    @Override
    @Transactional
    public Long createTodo(Long memberId, TodoCreateDto todoCreateDto) {
        Member member = memberQueryService.getMember(memberId);

        Integer maxOrder = todoRepository.findMaxSortOrderByMemberIdAndDate(memberId, todoCreateDto.getScheduledDate());
        int newOrder = (maxOrder != null ? maxOrder : -1) + 1;

        Todo todo = Todo.toEntity(todoCreateDto, member, newOrder);
        Todo savedTodo = todoRepository.save(todo);
        return savedTodo.getId();
    }

    @Override
    @Transactional
    public Long createTodoByOpu(Long memberId, Long opuId, OpuTodoCreateDto opuTodoCreateDto) {
        Member member = memberQueryService.getMember(memberId);
        Opu opu = opuQueryService.getOpu(opuId);

        int newOrder = getOrder(memberId, opuTodoCreateDto.getScheduledDate());

        memberOpuEventService.createEvent(member, opu);
        Todo todo = Todo.toEntity(opu, opuTodoCreateDto, member, newOrder);
        Todo savedTodo = todoRepository.save(todo);

        return savedTodo.getId();
    }

    int getOrder(Long memberId,LocalDate date) {
        Integer maxOrder = todoRepository.findMaxSortOrderByMemberIdAndDate(memberId,date);
        return (maxOrder != null ? maxOrder : -1) + 1;
    }

    @Override
    @Transactional
    public void createTodoByRoutine(Member member, Routine routine) {
        Set<LocalDate> dates = routineDateCalculator.getDates(routine);
        for (LocalDate date : dates) {
            saveTodo(member, routine, date, routine.getAlarmTime());
        }
    }

    @Override
    public void updateTodoByRoutine(Long routineId, String title, LocalTime alarmTime) {
        todoRepository.updateTodoByRoutine(routineId, title, alarmTime);
    }

    // 루틴 변경 시 날짜 diff 기반 업데이트
    @Override
    @Transactional
    public void updateTodoByRoutineChange(Member member, Routine routine, RoutineUpdateScope scope) {

        List<Todo> existingTodos =
                todoRepository.findByRoutine_IdAndDeletedAtIsNull(routine.getId());

        Set<LocalDate> existingDates = existingTodos.stream()
                .map(Todo::getScheduledDate)
                .collect(Collectors.toSet());

        Set<LocalDate> newDates = routineDateCalculator.getDates(routine);

        // 삭제 정책 - scope가 all 이면 모두 삭제, scope가 uncompleted면 uncompleted만 삭제 후 나머지는 연결 해제
        Set<LocalDate> toDelete = existingDates.stream()
                .filter(d -> !newDates.contains(d))
                .collect(Collectors.toSet());

        Set<LocalDate> toCreate = newDates.stream()
                .filter(d -> !existingDates.contains(d))
                .collect(Collectors.toSet());

        // 삭제 처리 - 정책에 따름
        for (Todo todo : existingTodos) {
            LocalDate date = todo.getScheduledDate();

            if (!toDelete.contains(date)) continue;  // 삭제 대상 날짜가 아니면 skip

            switch (scope) {
                case ALL -> todo.softDelete();
                case UNCOMPLETED_TODO -> {
                    if (!todo.isCompleted()) {
                        todo.softDelete();
                    } else {
                        todo.unlinkRoutine();
                    }
                }
                default -> throw new BusinessException(ErrorCode.ROUTINE_UPDATE_SCOPE_INVALID);
            }
        }

        // 신규 생성
        for (LocalDate d : toCreate) {
            saveTodo(member, routine, d, routine.getAlarmTime());
        }
    }

    private void saveTodo(Member member, Routine routine, LocalDate date, LocalTime time) {
        int sortOrder = getOrder(member.getId(), date);

        Todo todo = Todo.toEntity(member, routine, date, time, sortOrder);
        todoRepository.save(todo);
    }

    @Override
    @Transactional
    public void updateTodo(Long memberId, TodoUpdateDto dto, Long todoId) {
        Member member = memberQueryService.getMember(memberId);
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TODO_NOT_FOUND));

        if (!todo.isOwnedBy(member)) {
            throw new BusinessException(ErrorCode.TODO_FORBIDDEN);
        }

        // opu 인 경우 title 수정 불가
        if (todo.getOpu() != null && dto.getTitle() != null) {
            throw new BusinessException(ErrorCode.OPU_TODO_CANNOT_BE_MODIFIED);
        }

        todo.patch(dto.getTitle(), dto.getScheduledDate(), dto.getScheduledTime());
        if (todo.getRoutine() != null) {
            todo.unlinkRoutine();
        }

        // todo : todo 수정 정책 반영
    }

    @Override
    @Transactional
    public void updateStatus(Long memberId, TodoStatusUpdateDto dto, Long todoId) {
        Member member = memberQueryService.getMember(memberId);
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TODO_NOT_FOUND));

        if (!todo.isOwnedBy(member)) {
            throw new BusinessException(ErrorCode.TODO_FORBIDDEN);
        }

        // 이미 상태가 같은 경우 → 무시 (idempotent; 멱등성)
        if (todo.isCompleted() == dto.getCompleted()) {
            return;
        }

        // opu 인 todos 완료 시
        if (todo.getOpu() != null) {
            memberOpuCounterService.completeTodo(member, todo.getOpu());
            memberOpuEventService.completeEvent(member, todo.getOpu());
        }

        todo.updateStatus(dto);
    }

    @Override
    @Transactional
    public void deleteTodo(Long memberId, Long todoId) {
        Member member = memberQueryService.getMember(memberId);
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TODO_NOT_FOUND));

        if (!todo.isOwnedBy(member)) {
            throw new BusinessException(ErrorCode.TODO_FORBIDDEN);
        }

        todo.softDelete();
    }

    @Override
    @Transactional
    public void reorderTodo(Long memberId, int newOrder, Long todoId) {
        Member member = memberQueryService.getMember(memberId);
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TODO_NOT_FOUND));

        if (!todo.isOwnedBy(member)) {
            throw new BusinessException(ErrorCode.TODO_FORBIDDEN);
        }

        LocalDate date = todo.getScheduledDate();
        int oldOrder = todo.getSortOrder();
        if (oldOrder == newOrder) return; // idempotent 처리

        if (oldOrder < newOrder) {
            todoRepository.incrementSortOrderBetween(memberId, date, oldOrder + 1, newOrder, -1);
        } else {
            todoRepository.incrementSortOrderBetween(memberId, date, newOrder, oldOrder - 1, 1);
        }

        todo.setSortOrder(newOrder);
    }

    @Override
    @Transactional
    public void clearOpuFromTodos(Long opuId) {
        todoRepository.clearOpuFromTodos(opuId);
    }

    @Override
    public void deleteUncompletedTodoByRoutine(Long routineId) {
        todoRepository.findByRoutine_IdAndDeletedAtIsNullAndCompletedFalse(routineId)
                .forEach(Todo::softDelete);
    }

    @Override
    public void deleteTodoByRoutine(Long routineId) {
        todoRepository.findByRoutine_IdAndDeletedAtIsNull(routineId)
                .forEach(Todo::softDelete);
    }

    @Override
    public void unlinkToRoutine(Long routineId) {
        todoRepository.unlinkToRoutine(routineId);
    }

    @Override
    public void deleteTodoByRoutineAfterDate(Long routineId, LocalDate afterDate) {
        todoRepository.deleteTodoByRoutineIdAfterDate(routineId, afterDate);
    }
}