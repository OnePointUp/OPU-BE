package com.opu.opube.feature.auth.command.application.service;

import com.opu.opube.common.email.EmailService;
import com.opu.opube.common.jwt.JwtEmailTokenProvider;
import com.opu.opube.common.jwt.JwtTokenProvider;
import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.auth.command.application.dto.request.*;
import com.opu.opube.feature.auth.command.application.dto.response.KakaoLoginResponse;
import com.opu.opube.feature.auth.command.application.dto.response.KakaoTokenResponse;
import com.opu.opube.feature.auth.command.application.dto.response.KakaoUserInfoResponse;
import com.opu.opube.feature.auth.command.application.dto.response.TokenResponse;
import com.opu.opube.feature.auth.command.application.util.EmailHtmlBuilder;
import com.opu.opube.feature.auth.command.domain.service.AuthDomainService;
import com.opu.opube.feature.auth.command.domain.service.NicknameTagGenerator;
import com.opu.opube.feature.auth.command.infrastructure.oauth.KakaoOAuthClient;
import com.opu.opube.feature.member.command.domain.aggregate.AuthProvider;
import com.opu.opube.feature.member.command.domain.aggregate.Authorization;
import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.member.command.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthCommandServiceImpl implements AuthCommandService {

    private static final String ICON_PATH = "/icon/icon.png";
    private static final String EMAIL_VERIFY_PATH = "/api/v1/auth/verify";
    private static final String PASSWORD_RESET_PATH = "/reset-password";

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEmailTokenProvider emailTokenProvider;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final RefreshTokenService refreshTokenService;
    private final KakaoOAuthClient kakaoOAuthClient;
    private final NicknameTagGenerator nicknameTagGenerator;
    private final AuthDomainService authDomainService;

    @Value("${aws.s3.cloudfront-domain}")
    private String cloudfrontDomain;

    private String getIconUrl() {
        return "https://" + cloudfrontDomain + ICON_PATH;
    }

    private TokenResponse createTokenResponse(Long memberId) {
        String accessToken = jwtTokenProvider.createAccessToken(memberId);
        String refreshToken = jwtTokenProvider.createRefreshToken(memberId);

        long accessExpSec = jwtTokenProvider.getAccessExpirationSeconds();
        long refreshExpSec = jwtTokenProvider.getRefreshExpirationSeconds();

        refreshTokenService.save(memberId, refreshToken, refreshExpSec);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresInSeconds(accessExpSec)
                .refreshExpiresInSeconds(refreshExpSec)
                .build();
    }

    private void sendEmailAfterCommit(String email, String subject, String html, Long memberId, String operation) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
                        emailService.sendHtml(email, subject, html);
                        log.info("{} 이메일 발송 요청 완료. memberId={}", operation, memberId);
                    } catch (Exception ex) {
                        log.error("{} 이메일 발송 실패 (memberId={})", operation, memberId, ex);
                    }
                }
            });
        } else {
            try {
                emailService.sendHtml(email, subject, html);
            } catch (Exception ex) {
                log.error("동기 환경에서 {} 이메일 발송 실패 (memberId={})", operation, memberId, ex);
            }
        }
    }

    @Override
    @Transactional
    public Long register(RegisterRequest req, String backendBaseUrl) {
        authDomainService.validatePassword(req.getPassword());
        authDomainService.validateNickname(req.getNickname());

        String nicknameTag = nicknameTagGenerator.generate(req.getNickname());
        boolean webPushAgreed = Boolean.TRUE.equals(req.getWebPushAgreed());

        if (memberRepository.existsByEmail(req.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL, "이미 가입된 이메일이 존재합니다.");
        }

        Member m = Member.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .nickname(req.getNickname())
                .nicknameTag(nicknameTag)
                .authorization(Authorization.MEMBER)
                .authProvider("local")
                .emailVerified(false)
                .webPushAgreed(webPushAgreed)
                .build();

        Member saved = memberRepository.save(m);

        String token = emailTokenProvider.createEmailVerifyToken(saved.getId());

        Date issuedAt = emailTokenProvider.getIssuedAt(token);
        saved.updateEmailVerifyIssuedAt(
                issuedAt.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
        );

        String verifyUrl = backendBaseUrl + EMAIL_VERIFY_PATH + "?token=" + token;
        String html = EmailHtmlBuilder.buildVerificationHtml(saved.getNickname(), verifyUrl, getIconUrl());

        sendEmailAfterCommit(saved.getEmail(), "OPU 이메일 인증", html, saved.getId(), "회원가입");

        return saved.getId();
    }

    @Override
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

        return createTokenResponse(member.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public TokenResponse refreshToken(RefreshTokenRequest req) {
        String refreshToken = req.getRefreshToken();

        jwtTokenProvider.validateToken(refreshToken);

        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN, "refresh 토큰이 아닙니다.");
        }

        Long memberId = jwtTokenProvider.parseMemberId(refreshToken);

        String storedToken = refreshTokenService.get(memberId);
        if (storedToken == null) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
        if (!storedToken.equals(refreshToken)) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_MISMATCH);
        }

        return createTokenResponse(memberId);
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        if (!emailTokenProvider.isEmailVerifyToken(token)) {
            throw new BusinessException(ErrorCode.INVALID_EMAIL_VERIFY_TOKEN);
        }

        Long memberId = emailTokenProvider.parseMemberIdFromToken(token);
        Date issuedAt = emailTokenProvider.getIssuedAt(token);

        Member m = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.MEMBER_NOT_FOUND,
                        "존재하지 않는 회원입니다."
                ));

        if (m.getEmailVerifyIssuedAt() != null) {
            LocalDateTime tokenIat =
                    issuedAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            if (tokenIat.isBefore(m.getEmailVerifyIssuedAt())) {
                throw new BusinessException(ErrorCode.EMAIL_VERIFY_TOKEN_EXPIRED);
            }
        }

        m.verifyEmail();
    }

    @Override
    @Transactional
    public void requestPasswordReset(PasswordResetRequest req, String frontendBaseUrl) {
        String email = req.getEmail();

        Member member = memberRepository.findByEmail(email)
                .orElse(null);

        if (member == null) {
            log.info("비밀번호 재설정 요청 - 존재하지 않는 이메일: {}", email);
            return;
        }

        String token = emailTokenProvider.createPasswordResetToken(member.getId());

        Date issuedAt = emailTokenProvider.getIssuedAt(token);
        member.updatePasswordResetIssuedAt(
                issuedAt.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
        );

        String resetUrl = frontendBaseUrl + PASSWORD_RESET_PATH + "?token=" + token;
        String html = EmailHtmlBuilder.buildPasswordResetHtml(member.getNickname(), resetUrl, getIconUrl());

        sendEmailAfterCommit(member.getEmail(), "OPU 비밀번호 재설정 안내", html, member.getId(), "비밀번호 재설정");
    }

    @Override
    @Transactional
    public void resetPassword(PasswordResetConfirmRequest req) {
        String token = req.getToken();

        if (!emailTokenProvider.isPasswordResetToken(token)) {
            throw new BusinessException(
                    ErrorCode.INVALID_PASSWORD_RESET_TOKEN,
                    "비밀번호 재설정 토큰이 아닙니다."
            );
        }

        Long memberId = emailTokenProvider.parseMemberIdFromToken(token);
        Date issuedAt = emailTokenProvider.getIssuedAt(token);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (member.getPasswordResetIssuedAt() != null) {
            LocalDateTime tokenIat =
                    issuedAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            if (tokenIat.isBefore(member.getPasswordResetIssuedAt())) {
                throw new BusinessException(ErrorCode.PASSWORD_RESET_TOKEN_EXPIRED);
            }
        }

        authDomainService.validatePassword(req.getNewPassword());

        member.changePassword(passwordEncoder.encode(req.getNewPassword()));
        member.updatePasswordResetIssuedAt(LocalDateTime.now());
        refreshTokenService.delete(member.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public KakaoLoginResponse kakaoLogin(String code) {
        KakaoTokenResponse tokenResponse = kakaoOAuthClient.requestToken(code);
        KakaoUserInfoResponse userInfo = kakaoOAuthClient.requestUserInfo(tokenResponse.getAccessToken());
        Long kakaoId = userInfo.getId();
        String providerId = String.valueOf(kakaoId);

        Member member = memberRepository.findByAuthProviderAndProviderId("kakao", providerId)
                .orElse(null);

        if (member == null) {
            return KakaoLoginResponse.builder()
                    .needAdditionalInfo(true)
                    .providerId(providerId)
                    .build();
        }

        TokenResponse token = createTokenResponse(member.getId());

        return KakaoLoginResponse.builder()
                .needAdditionalInfo(false)
                .providerId(providerId)
                .token(token)
                .build();
    }

    @Override
    @Transactional
    public TokenResponse kakaoRegister(KakaoRegisterRequest req) {
        String providerId = req.getProviderId();

        authDomainService.validateNickname(req.getNickname());

        String nicknameTag = nicknameTagGenerator.generate(req.getNickname());
        boolean webPushAgreed = Boolean.TRUE.equals(req.getWebPushAgreed());

        if (memberRepository.findByAuthProviderAndProviderId("kakao", providerId).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_PROVIDER_MEMBER, "이미 가입된 카카오 계정입니다.");
        }

        Member newMember = Member.builder()
                .email(null)
                .password(null)
                .nickname(req.getNickname())
                .nicknameTag(nicknameTag)
                .authorization(Authorization.MEMBER)
                .authProvider("kakao")
                .providerId(providerId)
                .emailVerified(true)
                .webPushAgreed(webPushAgreed)
                .build();

        Member saved = memberRepository.save(newMember);

        return createTokenResponse(saved.getId());
    }

    @Override
    @Transactional
    public void resendVerificationEmail(String email, String backendBaseUrl) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.MEMBER_NOT_FOUND,
                        "가입된 회원이 아닙니다."
                ));

        if (member.isEmailVerified()) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_VERIFIED);
        }

        String token = emailTokenProvider.createEmailVerifyToken(member.getId());

        Date issuedAt = emailTokenProvider.getIssuedAt(token);
        member.updateEmailVerifyIssuedAt(
                issuedAt.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
        );

        String verifyUrl = backendBaseUrl + EMAIL_VERIFY_PATH + "?token=" + token;
        String html = EmailHtmlBuilder.buildVerificationHtml(member.getNickname(), verifyUrl, getIconUrl());

        sendEmailAfterCommit(member.getEmail(), "OPU 이메일 인증", html, member.getId(), "이메일 인증 재전송");
    }

    @Override
    @Transactional
    public void changePassword(Long memberId, ChangePasswordRequest req) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(req.getOldPassword(), member.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD, "기존 비밀번호가 일치하지 않습니다.");
        }

        authDomainService.validatePassword(req.getNewPassword());

        member.changePassword(passwordEncoder.encode(req.getNewPassword()));
        refreshTokenService.delete(memberId);
    }

    @Override
    @Transactional(readOnly = true)
    public void checkCurrentPassword(Long memberId, String rawPassword) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(rawPassword, member.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }
    }

    @Override
    @Transactional
    public void logout(Long memberId) {
        refreshTokenService.delete(memberId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailVerified(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.MEMBER_NOT_FOUND, "회원 정보를 찾을 수 없습니다."));

        return member.isEmailVerified();
    }

    @Override
    public void unlinkSocialIfNeeded(Member member) {
        String provider = member.getAuthProvider();
        String providerId = member.getProviderId();

        if (providerId == null) {
            return;
        }

        if (AuthProvider.KAKAO.equalsIgnoreCase(provider)) {
            kakaoOAuthClient.unlink(providerId);
        }
    }
}

