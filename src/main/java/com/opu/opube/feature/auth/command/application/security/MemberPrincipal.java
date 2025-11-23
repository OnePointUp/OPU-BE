package com.opu.opube.feature.auth.command.application.security;

import com.opu.opube.feature.member.command.domain.aggregate.Authorization;
import com.opu.opube.feature.member.command.domain.aggregate.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class MemberPrincipal implements UserDetails {

    private final Long memberId;
    private final String email;
    private final String nickname;
    private final String profileImageUrl;
    private final Authorization authorization;

    public MemberPrincipal(Member member) {
        this.memberId = member.getId();
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.profileImageUrl = member.getProfileImageUrl();
        this.authorization = member.getAuthorization();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + authorization.name()));
    }

    @Override public String getPassword() { return null; }
    @Override public String getUsername() { return email != null ? email : String.valueOf(memberId); }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}