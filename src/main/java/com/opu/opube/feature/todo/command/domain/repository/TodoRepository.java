package com.opu.opube.feature.todo.command.domain.repository;

import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.todo.command.domain.aggregate.Todo;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    /**
     * 특정 회원과 날짜의 최대 sortOrder 조회
     */
    @Query("SELECT MAX(t.sortOrder) FROM Todo t WHERE t.member.id = :memberId AND t.scheduledDate = :date")
    Integer findMaxSortOrderByMemberIdAndDate(@Param("memberId") Long memberId,
                                              @Param("date") LocalDate date);

    /**
     * 특정 날짜와 회원의 sortOrder 구간을 delta 만큼 증가/감소
     */
    @Modifying
    @Query("""
        UPDATE Todo t
        SET t.sortOrder = t.sortOrder + :delta
        WHERE t.member.id = :memberId
          AND t.scheduledDate = :date
          AND t.sortOrder BETWEEN :start AND :end
        """)
    void incrementSortOrderBetween(
            @Param("memberId") Long memberId,
            @Param("date") LocalDate date,
            @Param("start") int start,
            @Param("end") int end,
            @Param("delta") int delta
    );
}