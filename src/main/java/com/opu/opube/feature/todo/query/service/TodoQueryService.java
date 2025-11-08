package com.opu.opube.feature.todo.query.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.opu.opube.feature.todo.query.infrastructure.repository.TodoQueryRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoQueryService {

    private final TodoQueryRepository todoQueryRepository;

}