package com.opu.opube.feature.opu.command.domain.aggregate;

import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.opu.command.application.dto.request.OpuRegisterDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Opu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false, nullable = false, length = 64)
    private String title;

    @Column(updatable = false, nullable = false)
    private String description;

    @Builder.Default
    @Column(nullable = false, length = 8) // VARCHAR(8) & NOT NULL
    private String emoji = "🍀";

    @Column(name = "required_minutes", updatable = false, nullable = false)
    private Integer requiredMinutes;

    @Column(nullable = false)
    private Boolean isShared;

    @Column(name = "created_at", updatable = false, nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private OpuCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void share() {
        if (Boolean.TRUE.equals(this.isShared)) {
            return;
        }
        this.isShared = true;
    }

    public void unshare() {
        if (Boolean.FALSE.equals(this.isShared)) {
            return;
        }
        this.isShared = false;
    }

    public static Opu toEntity(OpuRegisterDto dto, Member member, OpuCategory category) {
        return Opu.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .requiredMinutes(dto.getRequiredMinutes())
                .emoji(dto.getEmoji() != null ? dto.getEmoji() : "🍀")
                .isShared(dto.getIsShared())
                .category(category)
                .member(member)
                .build();
    }
}