package com.opu.opube.feature.opu.command.domain.aggregate;

import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.opu.command.application.dto.request.OpuRegisterDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberOpuEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", updatable = false, nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opu_id")
    private Opu opu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void setCompleted() {
        this.completedAt = LocalDateTime.now();
    }
}