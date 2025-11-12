package com.opu.opube.feature.notifiaction.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_type",
        indexes = {
                @Index(name = "idx_notification_type_code", columnList = "code")
        })
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", length = 50, nullable = false, unique = true)
    private String code; // e.g. ALL, MORNING, EVENING, ROUTINE, RANDOM_PICK

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "default_enabled", nullable = false)
    private boolean defaultEnabled = true;

    @Column(name = "default_time")
    private LocalTime defaultTime; // nullable

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}