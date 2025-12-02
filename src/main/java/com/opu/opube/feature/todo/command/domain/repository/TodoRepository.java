package com.opu.opube.feature.todo.command.domain.repository;

import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.todo.command.domain.aggregate.Todo;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    /**
     * 특정 회원과 날짜의 최대 sortOrder 조회
     */
    @Query("SELECT MAX(t.sortOrder) FROM Todo t WHERE t.member.id = :memberId AND t.scheduledDate = :date AND t.deletedAt IS NULL")
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
              AND t.deletedAt IS NULL
            """)
    void incrementSortOrderBetween(
            @Param("memberId") Long memberId,
            @Param("date") LocalDate date,
            @Param("start") int start,
            @Param("end") int end,
            @Param("delta") int delta
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Todo t SET t.opu = null WHERE t.opu.id = :opuId")
    void clearOpuFromTodos(@Param("opuId") Long opuId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
                    update Todo t
                    set
                        t.title = CASE
                                    WHEN :title IS NOT NULL THEN :title
                                    ELSE t.title
                                    END,
                        t.scheduledTime = CASE
                                    WHEN :alarmTime IS NOT NULL THEN :alarmTime
                                    ELSE t.scheduledTime
                                    END
                    where t.routine.id = :routineId
                    and t.deletedAt is null
            """)
    void updateTodoByRoutine(@Param("routineId") Long routineId, @Param("title") String title, @Param("alarmTime") LocalTime alarmTime);

    List<Todo> findByRoutine_IdAndDeletedAtIsNullAndCompletedFalse(Long routineId);

    List<Todo> findByRoutine_IdAndDeletedAtIsNull(Long routineId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Todo t SET t.routine = null WHERE t.routine.id = :routineId")
    void unlinkToRoutine(@Param("routineId") Long routineId);
}