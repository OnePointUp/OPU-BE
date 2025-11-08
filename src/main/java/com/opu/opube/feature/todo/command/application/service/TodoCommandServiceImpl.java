package com.opu.opube.feature.todo.command.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.opu.opube.feature.todo.command.domain.repository.TodoRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoCommandServiceImpl implements TodoCommandService {

    private final TodoRepository todoRepository;

}