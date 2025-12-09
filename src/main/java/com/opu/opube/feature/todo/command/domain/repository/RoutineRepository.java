package com.opu.opube.feature.todo.command.domain.repository;

import com.opu.opube.feature.todo.command.domain.aggregate.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RoutineRepository extends JpaRepository<Routine, Long> {
    @Modifying
    @Query("delete from Routine r where r.member.id = :memberId")
    void deleteByMemberId(Long memberId);
}