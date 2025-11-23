package com.opu.opube.feature.todo.command.domain.repository;

import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.todo.command.domain.aggregate.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    Integer findMaxSortOrderByMemberAndScheduledDate(Member member, LocalDate scheduledDate);
}