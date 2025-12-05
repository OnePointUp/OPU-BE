package com.opu.opube.feature.opu.command.domain.aggregate;

import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.opu.query.dto.request.OpuRandomSource;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "opu_random_draw_event")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpuRandomDrawEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opu_id")
    private Opu opu;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 16)
    private OpuRandomSource source;

    @Column(name = "required_minutes")
    private Integer requiredMinutes;

    @Column(name = "drawn_at", nullable = false)
    private LocalDateTime drawnAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}