package com.opu.opube.feature.todo.command.domain.aggregate;

import com.opu.opube.feature.todo.command.application.dto.request.RoutineCreateDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "routine_schedule")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutineSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "routine_id", nullable = false)
    private Routine routine;

    @Column(name = "week_days", length = 64)
    private String weekDays; // "0,1,2,3,4,5,6"

    @Column(name = "month_days", columnDefinition = "TEXT")
    private String monthDays; // "1,10,31,L"

    @Column(name = "days", columnDefinition = "TEXT")
    private String days; // "1-10,2-20"

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static RoutineSchedule toEntity(RoutineCreateDto dto, Routine routine) {
        return RoutineSchedule.builder()
                .routine(routine)
                .weekDays(dto.getWeekDays())
                .monthDays(dto.getMonthDays())
                .days(dto.getYearDays()) // days -> yearDays
                .build();
    }
}