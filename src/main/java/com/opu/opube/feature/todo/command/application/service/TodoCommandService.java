package com.opu.opube.feature.todo.command.application.service;

import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.todo.command.application.dto.request.*;
import com.opu.opube.feature.todo.command.domain.aggregate.Routine;

import java.time.LocalTime;

public interface TodoCommandService {
    Long createTodo(Long memberId, TodoCreateDto todoCreateDto);

    void updateTodo(Long memberId, TodoUpdateDto dto, Long todoId);

    void updateStatus(Long memberId, TodoStatusUpdateDto dto, Long todoId);

    void deleteTodo(Long memberId, Long todoId);

    void reorderTodo(Long memberId, int newOrder, Long todoId);

    Long createTodoByOpu(Long memberId, Long opuId, OpuTodoCreateDto opuTodoCreateDto);

    void createTodoByRoutine(Member member, Routine routine);

    void clearOpuFromTodos(Long opuId);

    void updateTodoByRoutine(Long routineId, String title, LocalTime alarmTime);

    void updateTodoByRoutineChange(Member member, Routine routine, RoutineScope scope);
}