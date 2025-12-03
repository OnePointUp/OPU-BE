package com.opu.opube.feature.opu.command.domain.aggregate;


import com.opu.opube.feature.member.command.domain.aggregate.Member;
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
public class MemberOpuCounter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(nullable = false)
    private Integer totalCompletions = 0;

    @Column(name = "created_at", updatable = false, nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opu_id", nullable = false)
    private Opu opu;

    public static MemberOpuCounter toEntity(Member member, Opu opu) {
        return MemberOpuCounter.builder()
                .member(member)
                .opu(opu)
                .build();
    }

    public void increaseCount() {
        totalCompletions += 1;
    }

    public void decreaseCount() {
        if (totalCompletions <= 0) {
            return;
        }
        totalCompletions -= 1;
    }
}
