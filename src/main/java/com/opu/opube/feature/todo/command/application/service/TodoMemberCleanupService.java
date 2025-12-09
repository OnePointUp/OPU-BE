package com.opu.opube.feature.todo.command.application.service;

import com.opu.opube.feature.todo.command.domain.repository.RoutineRepository;
import com.opu.opube.feature.todo.command.domain.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TodoMemberCleanupService {

    private final TodoRepository todoRepository;
    private final RoutineRepository routineRepository;

    @Transactional
    public void deleteByMemberId(Long memberId) {
        todoRepository.deleteByMemberId(memberId);
        routineRepository.deleteByMemberId(memberId);
    }
}