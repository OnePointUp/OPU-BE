package com.opu.opube.feature.todo.command.application.service;

import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.member.query.service.MemberQueryService;
import com.opu.opube.feature.todo.command.application.dto.request.TodoCreateDto;
import com.opu.opube.feature.todo.command.application.dto.request.TodoUpdateDto;
import com.opu.opube.feature.todo.command.domain.aggregate.Todo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.opu.opube.feature.todo.command.domain.repository.TodoRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoCommandServiceImpl implements TodoCommandService {

    private final TodoRepository todoRepository;
    private final MemberQueryService memberQueryService;

    @Override
    @Transactional
    public Long createTodo(Long memberId, TodoCreateDto todoCreateDto) {
        Member member = memberQueryService.getMember(memberId);
        Todo todo = Todo.toEntity(todoCreateDto, member);
        Todo savedTodo = todoRepository.save(todo);
        return savedTodo.getId();
    }

    @Override
    @Transactional
    public void updateTodo(Long memberId, TodoUpdateDto dto) {
        Member member = memberQueryService.getMember(memberId);
        Todo todo = todoRepository.findById(dto.getTodoId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TODO_NOT_FOUND));

        if (!todo.isOwnedBy(member)) {
            throw new BusinessException(ErrorCode.TODO_FORBIDDEN);
        }

        todo.patch(dto.getTitle(), dto.getScheduledDate(), dto.getScheduledTime());

    }
}