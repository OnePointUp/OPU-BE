package com.opu.opube.feature.todo.command.domain.aggregate;

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
    private String weekDays;

    @Column(name = "month_days", columnDefinition = "TEXT")
    private String monthDays;

    @Column(name = "days", columnDefinition = "TEXT")
    private String days;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

}