package com.opu.opube.feature.todo.command.domain.aggregate;

import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.todo.command.application.dto.request.RoutineCreateDto;
import com.opu.opube.feature.todo.command.application.dto.request.RoutineUpdateDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "routine")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Routine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 루틴 생성자
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(length = 8)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency", nullable = false, length = 16)
    private Frequency frequency;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "alarm_time")
    private LocalTime alarmTime;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "week_days", length = 64)
    private String weekDays; // "0,1,2,3,4,5,6"

    @Column(name = "month_days", columnDefinition = "TEXT")
    private String monthDays; // "1,10,31,L"

    @Column(name = "days", columnDefinition = "TEXT")
    private String days; // "1-10,2-20"

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static Routine toEntity(RoutineCreateDto dto, Member member) {
        return Routine.builder()
                .title(dto.getTitle())
                .frequency(dto.getFrequency())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .alarmTime(dto.getAlarmTime())
                .color(dto.getColor())
                .member(member)
                .weekDays(dto.getWeekDays())
                .monthDays(dto.getMonthDays())
                .days(dto.getYearDays())
                .build();
    }

    // 상태 조작 메서드
    public void activate() { this.active = true; }
    public void deactivate() { this.active = false; }
    public void updateAlarmTime(LocalTime t) { this.alarmTime = t; }

    public void update(RoutineUpdateDto dto) {
        if (dto.getTitle() != null) this.title = dto.getTitle();
        if (dto.getColor() != null) this.color = dto.getColor();
        if (dto.getAlarmTime() != null) this.alarmTime = dto.getAlarmTime();

        boolean isFrequencyChanged = dto.getFrequency() != null
                && dto.getFrequency() != this.frequency;
        if (isFrequencyChanged) {
            // 기존 패턴 초기화
            this.weekDays = null;
            this.monthDays = null;
            this.days = null;

            // frequency 변경 반영
            this.frequency = dto.getFrequency();

            // DTO 값 입력
            if (dto.getWeekDays() != null) this.weekDays = dto.getWeekDays();
            if (dto.getMonthDays() != null) this.monthDays = dto.getMonthDays();
            if (dto.getDays() != null) this.days = dto.getDays();
            return;
        }

        // frequency는 변경되지 않았지만 패턴만 변경하려는 경우 (PATCH)
        if (dto.getWeekDays() != null) this.weekDays = dto.getWeekDays();
        if (dto.getMonthDays() != null) this.monthDays = dto.getMonthDays();
        if (dto.getDays() != null) this.days = dto.getDays();
    }
}