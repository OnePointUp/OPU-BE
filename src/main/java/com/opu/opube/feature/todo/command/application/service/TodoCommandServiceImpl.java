package com.opu.opube.feature.todo.command.application.service;

import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.member.query.service.MemberQueryService;
import com.opu.opube.feature.opu.command.application.service.MemberOpuCounterService;
import com.opu.opube.feature.todo.command.application.dto.request.TodoCreateDto;
import com.opu.opube.feature.todo.command.application.dto.request.TodoStatusUpdateDto;
import com.opu.opube.feature.todo.command.application.dto.request.TodoUpdateDto;
import com.opu.opube.feature.todo.command.domain.aggregate.Todo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.opu.opube.feature.todo.command.domain.repository.TodoRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoCommandServiceImpl implements TodoCommandService {

    private final TodoRepository todoRepository;
    private final MemberQueryService memberQueryService;
    private final MemberOpuCounterService memberOpuCounterService;

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
    public void updateTodo(Long memberId, TodoUpdateDto dto, Long todoId) {
        Member member = memberQueryService.getMember(memberId);
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TODO_NOT_FOUND));

        if (!todo.isOwnedBy(member)) {
            throw new BusinessException(ErrorCode.TODO_FORBIDDEN);
        }

        // opu 인 경우 수정 불가
        if (todo.getOpu() != null) {
            throw new BusinessException(ErrorCode.OPU_TODO_CANNOT_BE_MODIFIED);
        }

        // todo : routine 인 경우

        todo.patch(dto.getTitle(), dto.getScheduledDate(), dto.getScheduledTime());

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

        }

        // routine 인 todos 완료 시

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

        // todo 멱등성 관리

        todoRepository.delete(todo);
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
}