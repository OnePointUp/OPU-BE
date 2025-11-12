package com.opu.opube.feature.member.command.application.service;

import com.opu.opube.common.email.EmailService;
import com.opu.opube.common.jwt.JwtEmailTokenProvider;
import com.opu.opube.common.jwt.JwtTokenProvider;
import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.member.command.application.dto.RegisterRequest;
import com.opu.opube.feature.member.command.application.dto.TokenResponse;
import com.opu.opube.feature.member.command.domain.aggregate.Authorization;
import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.member.command.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEmailTokenProvider tokenProvider;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;

    @Transactional
    public Long register(RegisterRequest req, String backendBaseUrl) {

        if (memberRepository.existsByEmail(req.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL, "이미 가입된 이메일이 존재합니다.");
        }

        Member m = Member.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .nickname(req.getNickname())
                .authorization(Authorization.MEMBER)
                .authProvider("local")
                .emailVerified(false)
                .build();

        Member saved = memberRepository.save(m);

        String token = tokenProvider.createTokenForMemberId(saved.getId());
        String verifyUrl = backendBaseUrl + "/api/v1/auth/verify?token=" + token;
        String html = buildVerificationHtml(saved.getNickname(), verifyUrl);

        // 트랜잭션 커밋 후 메일 발송
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
                        // 메일 전송: 실패 시 예외를 던지지 않음 (로그)
                        emailService.sendHtml(saved.getEmail(), "OPU 이메일 인증", html);
                        log.info("회원가입 이메일 발송 요청 완료. memberId={}", saved.getId());
                    } catch (Exception ex) {
                        log.error("회원가입 이메일 발송 실패 (memberId={})", saved.getId(), ex);
                    }
                }
            });
        } else {
            try {
                emailService.sendHtml(saved.getEmail(), "OPU 이메일 인증", html);
            } catch (Exception ex) {
                log.error("동기 환경에서 이메일 발송 실패 (memberId={})", saved.getId(), ex);
            }
        }

        return saved.getId();
    }

    @Transactional(readOnly = true)
    public TokenResponse login(String email, String rawPassword) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND, "가입된 회원이 아닙니다."));

        if (!passwordEncoder.matches(rawPassword, member.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD, "비밀번호가 일치하지 않습니다.");
        }

        if (!member.isEmailVerified()) {
            throw new BusinessException(ErrorCode.EMAIL_NOT_VERIFIED, "이메일 인증이 필요합니다.");
        }

        String access = jwtTokenProvider.createAccessToken(member.getId());
        String refresh = jwtTokenProvider.createRefreshToken(member.getId());

        return TokenResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .expiresInSeconds( (long) (jwtTokenProvider.getAccessExpirationSeconds()) )
                .build();
    }

    @Transactional
    public void verifyEmail(String token) {
        Long memberId = tokenProvider.parseMemberIdFromToken(token);
        Member m = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원"));
        m.verifyEmail();
        memberRepository.save(m);
    }

    private String buildVerificationHtml(String nickname, String verifyUrl) {
        return """
<html>
<body style="margin:0; padding:0; background:#f8f9fc; font-family: 'Apple SD Gothic Neo', 'Noto Sans KR', sans-serif;">
  <div style="max-width:480px; margin:40px auto; background:#fff; border-radius:12px; padding:32px 24px;
              box-shadow:0 4px 12px rgba(0,0,0,0.06);">

    <h2 style="margin:0 0 8px; font-size:22px; color:#1A1C1F; text-align:center;">
      앱 아이콘
    </h2>

    <p style="font-size:15px; color:#555; text-align:center; margin-bottom:24px; line-height:1.5;">
                      <span style="font-weight:700; color:#B8DD7C;">%s</span> 님, 환영합니다! 🍀<br/>
                      아래 버튼을 눌러 계정 인증을 완료해주세요.
                    </p>

    <a href="%s" target="_blank"
       style="display:block; width:100%%; background:#B8DD7C; color:#fff;
              text-decoration:none; padding:14px 0; border-radius:8px;
              font-size:16px; font-weight:600; text-align:center;
              box-shadow:0 2px 6px rgba(47,128,237,0.3);">
      이메일 인증하기
    </a>

 
    <hr style="border:none; border-top:1px solid #eee; margin:24px 0;" />

    <p style="font-size:12px; color:#aaa; text-align:center; margin:0;">
      본 메일은 발신 전용입니다.<br/>
      © 2025 OPU. All rights reserved.
    </p>

  </div>
</body>
</html>
"""
                .formatted(nickname, verifyUrl, verifyUrl);
    }
}