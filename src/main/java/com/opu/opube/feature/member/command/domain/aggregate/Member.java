package com.opu.opube.feature.member.command.domain.aggregate;

import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
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

    public static final String DEACTIVATED_NICKNAME = "탈퇴한 사용자";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "nickname", length = 50, nullable = false)
    private String nickname;

    @Column(name = "nickname_tag", length = 10)
    private String nicknameTag;

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

    @Column(name = "web_push_agreed", nullable = false)
    private Boolean webPushAgreed = false;

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

    @Column(name = "email_verify_issued_at")
    private LocalDateTime emailVerifyIssuedAt;

    @Column(name = "password_reset_issued_at")
    private LocalDateTime passwordResetIssuedAt;


    // 탈퇴 회원인지 확인
    public boolean isDeleted() {
        return deletedAt != null;
    }

    // 비활성화(탈퇴) 처리
    public void deactivate() {
        // 개인정보 최소화
        this.email = null;
        this.password = null;
        this.bio = null;
        this.profileImageUrl = null;
        this.webPushAgreed = false;

        // 소셜 재가입 허용을 위해 providerId 제거
        this.providerId = null;

        // 닉네임은 NOT NULL 이라서 기본값으로 덮어쓰기
        this.nickname = DEACTIVATED_NICKNAME;
        this.nicknameTag = null;

        this.deletedAt = LocalDateTime.now();
    }

    public void updateLastLogin(LocalDateTime at) {
        this.lastLogin = at;
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

    public void updateEmailVerifyIssuedAt(LocalDateTime issuedAt) {
        this.emailVerifyIssuedAt = issuedAt;
    }

    public void updatePasswordResetIssuedAt(LocalDateTime issuedAt) {
        this.passwordResetIssuedAt = issuedAt;
    }
    public void updateWebPushAgreed(boolean agreed) {
        this.webPushAgreed = agreed;
    }
    public boolean isWebPushAgreed() {
        return this.webPushAgreed;
    }

    public void updateNicknameAndTag(String newNickname, String newTag) {
        if (newNickname != null && !newNickname.isBlank()) {
            if (newNickname.length() < 2 || newNickname.length() > 20) {
                throw new BusinessException(ErrorCode.INVALID_NICKNAME_LENGTH);
            }
            this.nickname = newNickname;
        }
        if (newTag != null && !newTag.isBlank()) {
            this.nicknameTag = newTag;
        }
    }

    public void updateProfile(String bio, String profileImageUrl) {
        if (bio != null && bio.length() > 100) {
            throw new BusinessException(ErrorCode.INVALID_BIO_LENGTH);
        }
        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
    }
}