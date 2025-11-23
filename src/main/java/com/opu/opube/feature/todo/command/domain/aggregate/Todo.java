package com.opu.opube.feature.todo.command.domain.aggregate;

import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.opu.command.domain.aggregate.Opu;
import com.opu.opube.feature.todo.command.application.dto.request.TodoCreateDto;
import com.opu.opube.feature.todo.command.application.dto.request.TodoUpdateDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "todo")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id")
    private Routine routine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opu_id")
    private Opu opu;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "scheduled_time")
    private LocalTime scheduledTime;

    @Builder.Default
    @Column(name = "is_completed", nullable = false)
    private boolean completed = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static Todo toEntity(TodoCreateDto todoCreateDto, Member member) {
        return Todo.builder()
                .title(todoCreateDto.getTitle())
                .scheduledDate(todoCreateDto.getScheduledDate())
                .scheduledTime(todoCreateDto.getScheduledTime())
                .member(member)
                .build();
    }

    public void patch(String title, LocalDate scheduledDate, LocalTime scheduledTime) {
        if (title != null) this.title = title;
        if (scheduledDate != null) this.scheduledDate = scheduledDate;
        if (scheduledDate != null) this.scheduledTime = scheduledTime;
    }

    public void markCompleted() { this.completed = true; }
    public void markUncompleted() { this.completed = false; }

    public boolean isOwnedBy(Member member) {
        return this.member != null && member != null
                && this.member.getId().equals(member.getId());
    }
}