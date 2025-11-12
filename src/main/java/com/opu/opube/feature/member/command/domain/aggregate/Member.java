package com.opu.opube.feature.member.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "nickname", length = 50, nullable = false)
    private String nickname;

    @Column(name = "profile_image_url", length = 512)
    private String profileImageUrl;

    @Column(name = "bio", length = 200)
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(name = "authorization", length = 16, nullable = false)
    private Authorization authorization = Authorization.MEMBER;

    @Column(name = "auth_provider", length = 32, nullable = false)
    private String authProvider = "local";

    @Column(name = "provider_id", length = 255)
    private String providerId;

    @Column(name = "is_email_verified", nullable = false)
    private boolean emailVerified = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;


    public void markDeleted() {
        this.deletedAt = LocalDateTime.now();
    }

    public void updateLastLogin(LocalDateTime at) {
        this.lastLogin = at;
    }

    public void updateProfile(String nickname, String profileImageUrl, String bio) {
        if (nickname != null) this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.bio = bio;
    }

    public void verifyEmail() {
        this.emailVerified = true;
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member)) return false;
        Member other = (Member) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}