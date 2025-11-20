package com.opu.opube.feature.notification.command.domain.aggregate;

import com.opu.opube.feature.member.command.domain.aggregate.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "web_push_subscription",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_member_endpoint",
                        columnNames = {"member_id", "endpoint"}
                )
        }
)
public class WebPushSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 500)
    private String endpoint;

    @Column(nullable = false, length = 255)
    private String p256dh;

    @Column(nullable = false, length = 255)
    private String auth;

    @Column(name = "expiration_time")
    private LocalDateTime expirationTime;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 구독 정보 갱신용 메서드
    public void updateKeys(String endpoint, String p256dh, String auth, LocalDateTime expirationTime) {
        this.endpoint = endpoint;
        this.p256dh = p256dh;
        this.auth = auth;
        this.expirationTime = expirationTime;
    }
}