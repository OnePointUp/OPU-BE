package com.opu.opube.feature.auth.command.application.security;

import com.opu.opube.common.jwt.JwtTokenProvider;
import com.opu.opube.exception.BusinessException;
import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.member.command.domain.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        if (token != null) {
            try {
                // 1) 유효성 검증 (서명, 만료, 형식 등)
                jwtTokenProvider.validateToken(token);

                // 2) access 토큰만 인증에 사용 (refresh 토큰 들어오면 무시)
                if (!jwtTokenProvider.isAccessToken(token)) {
                    filterChain.doFilter(request, response);
                    return;
                }

                // 3) memberId 추출
                Long memberId = jwtTokenProvider.parseMemberId(token);

                // 4) Member 조회
                Member member = memberRepository.findById(memberId).orElse(null);
                if (member != null) {
                    MemberPrincipal principal = new MemberPrincipal(member);

                    Authentication authentication =
                            new UsernamePasswordAuthenticationToken(
                                    principal,
                                    null,
                                    principal.getAuthorities()
                            );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

            } catch (BusinessException ex) {
                 response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                 return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}