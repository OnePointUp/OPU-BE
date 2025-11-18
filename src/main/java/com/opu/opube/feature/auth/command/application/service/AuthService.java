package com.opu.opube.feature.auth.command.application.service;

import com.opu.opube.common.email.EmailService;
import com.opu.opube.common.jwt.JwtEmailTokenProvider;
import com.opu.opube.common.jwt.JwtTokenProvider;
import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.auth.command.application.dto.request.*;
import com.opu.opube.feature.auth.command.application.dto.response.TokenResponse;
import com.opu.opube.feature.auth.command.application.dto.response.KakaoLoginResponse;
import com.opu.opube.feature.auth.command.application.dto.response.KakaoTokenResponse;
import com.opu.opube.feature.auth.command.application.dto.response.KakaoUserInfoResponse;
import com.opu.opube.feature.auth.command.config.KakaoOAuthProperties;
import com.opu.opube.feature.member.command.domain.aggregate.Authorization;
import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.member.command.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEmailTokenProvider tokenProvider;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final RefreshTokenService refreshTokenService;
    private final KakaoOAuthProperties kakaoProps;
    private final WebClient webClient;


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
                .profileImageUrl(req.getProfileImageUrl())
                .build();

        Member saved = memberRepository.save(m);

        // ✅ 이메일 인증용 토큰 생성 (createEmailVerifyToken 사용)
        String token = tokenProvider.createEmailVerifyToken(saved.getId());
        String verifyUrl = backendBaseUrl + "/api/v1/auth/verify?token=" + token;
        String html = buildVerificationHtml(saved.getNickname(), verifyUrl);

        // 트랜잭션 커밋 후 메일 발송
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
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

        String accessToken = jwtTokenProvider.createAccessToken(member.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());

        long accessExpSec = jwtTokenProvider.getAccessExpirationSeconds();
        long refreshExpSec = jwtTokenProvider.getRefreshExpirationSeconds();

        refreshTokenService.save(member.getId(), refreshToken, refreshExpSec);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresInSeconds(accessExpSec)
                .refreshExpiresInSeconds(refreshExpSec)
                .build();
    }


    @Transactional(readOnly = true)
    public TokenResponse refreshToken(RefreshTokenRequest req) {
        String refreshToken = req.getRefreshToken();

        // 1) 기본 검증 (서명/만료 등)
        jwtTokenProvider.validateToken(refreshToken);

        // 2) 타입 확인
        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN, "refresh 토큰이 아닙니다.");
        }

        // 3) memberId 추출
        Long memberId = jwtTokenProvider.parseMemberId(refreshToken);

        // 4) Redis에 저장된 토큰과 비교
        String storedToken = refreshTokenService.get(memberId);
        if (storedToken == null) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
        if (!storedToken.equals(refreshToken)) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_MISMATCH);
        }

        // 5) 새 토큰 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(memberId);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(memberId);

        long accessExpSec = jwtTokenProvider.getAccessExpirationSeconds();
        long refreshExpSec = jwtTokenProvider.getRefreshExpirationSeconds();

        // 6) Redis에 refreshToken 갱신
        refreshTokenService.save(memberId, newRefreshToken, refreshExpSec);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresInSeconds(accessExpSec)
                .refreshExpiresInSeconds(refreshExpSec)
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


    @Transactional
    public void requestPasswordReset(PasswordResetRequest req, String frontendBaseUrl) {
        String email = req.getEmail();

        Member member = memberRepository.findByEmail(email)
                .orElse(null);

        if (member == null) {
            log.info("비밀번호 재설정 요청 - 존재하지 않는 이메일: {}", email);
            return;
        }

        String token = tokenProvider.createPasswordResetToken(member.getId());

        String resetUrl = frontendBaseUrl + "/reset-password?token=" + token;

        String html = buildPasswordResetHtml(member.getNickname(), resetUrl);

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
                        emailService.sendHtml(member.getEmail(), "OPU 비밀번호 재설정 안내", html);
                        log.info("비밀번호 재설정 이메일 발송 완료. memberId={}", member.getId());
                    } catch (Exception ex) {
                        log.error("비밀번호 재설정 이메일 발송 실패 (memberId={})", member.getId(), ex);
                    }
                }
            });
        } else {
            try {
                emailService.sendHtml(member.getEmail(), "OPU 비밀번호 재설정 안내", html);
            } catch (Exception ex) {
                log.error("동기 환경에서 비밀번호 재설정 이메일 발송 실패 (email={})", email, ex);
            }
        }
    }


    @Transactional
    public void resetPassword(PasswordResetConfirmRequest req) {
        String token = req.getToken();

        if (!tokenProvider.isPasswordResetToken(token)) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD_RESET_TOKEN, "비밀번호 재설정 토큰이 아닙니다.");
        }

        Long memberId = tokenProvider.parseMemberIdFromToken(token);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // 필요 시 규칙 강화
        String rawPassword = req.getNewPassword();
        if (rawPassword == null || rawPassword.length() < 8) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD, "비밀번호는 8자 이상이어야 합니다.");
        }

        member.changePassword(passwordEncoder.encode(rawPassword));

        refreshTokenService.delete(member.getId()); // 구현되어 있으면 사용
    }

    //카카오 로그인
    @Transactional(readOnly = true)
    public KakaoLoginResponse kakaoLogin(String code) {
        // 1) 인가 코드 → Kakao Access Token
        KakaoTokenResponse tokenResponse = requestKakaoToken(code);

        // 2) Access Token으로 사용자 정보 조회 (id만 사용)
        KakaoUserInfoResponse userInfo = requestKakaoUserInfo(tokenResponse.getAccessToken());
        Long kakaoId = userInfo.getId();
        String providerId = String.valueOf(kakaoId);

        // 3) 기존 회원 조회
        Member member = memberRepository.findByAuthProviderAndProviderId("kakao", providerId)
                .orElse(null);

        // 4) 신규 회원이라면 추가 정보 필요
        if (member == null) {
            return KakaoLoginResponse.builder()
                    .needAdditionalInfo(true)
                    .providerId(providerId)
                    .build();
        }

        // 5) 기존 회원 → JWT 발급
        String accessToken = jwtTokenProvider.createAccessToken(member.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());

        long accessExpSec = jwtTokenProvider.getAccessExpirationSeconds();
        long refreshExpSec = jwtTokenProvider.getRefreshExpirationSeconds();

        refreshTokenService.save(member.getId(), refreshToken, refreshExpSec);

        return KakaoLoginResponse.builder()
                .needAdditionalInfo(false)
                .providerId(providerId)
                .token(
                        TokenResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .tokenType("Bearer")
                                .expiresInSeconds(accessExpSec)
                                .refreshExpiresInSeconds(refreshExpSec)
                                .build()
                )
                .build();
    }


    @Transactional
    public TokenResponse kakaoRegister(KakaoRegisterRequest req) {
        String providerId = req.getProviderId();

        // 이미 가입된 providerId이면 예외
        if (memberRepository.findByAuthProviderAndProviderId("kakao", providerId).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_PROVIDER_MEMBER, "이미 가입된 카카오 계정입니다.");
        }

        Member newMember = Member.builder()
                .email(null)
                .password(null)
                .nickname(req.getNickname())
                .authorization(Authorization.MEMBER)
                .authProvider("kakao")
                .providerId(providerId)
                .emailVerified(true) // 소셜 로그인은 바로 인증된 것으로 처리
                .profileImageUrl(req.getProfileImageUrl())
                .build();

        Member saved = memberRepository.save(newMember);

        String accessToken = jwtTokenProvider.createAccessToken(saved.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(saved.getId());

        long accessExpSec = jwtTokenProvider.getAccessExpirationSeconds();
        long refreshExpSec = jwtTokenProvider.getRefreshExpirationSeconds();

        refreshTokenService.save(saved.getId(), refreshToken, refreshExpSec);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresInSeconds(accessExpSec)
                .refreshExpiresInSeconds(refreshExpSec)
                .build();
    }


    private KakaoTokenResponse requestKakaoToken(String code) {

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("client_id", kakaoProps.getClientId());
        form.add("redirect_uri", kakaoProps.getRedirectUri());
        form.add("code", code);

        if (StringUtils.hasText(kakaoProps.getClientSecret())) {
            form.add("client_secret", kakaoProps.getClientSecret());
        }

        KakaoTokenResponse tokenResponse = webClient.post()
                .uri(kakaoProps.getTokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(form))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .map(body -> new BusinessException(
                                        ErrorCode.OAUTH_LOGIN_FAILED,
                                        "카카오 토큰 발급 실패: " + body
                                ))
                )
                .bodyToMono(KakaoTokenResponse.class)
                .block();

        if (tokenResponse == null || !StringUtils.hasText(tokenResponse.getAccessToken())) {
            throw new BusinessException(ErrorCode.OAUTH_LOGIN_FAILED, "카카오 토큰 발급에 실패했습니다.");
        }

        return tokenResponse;
    }

    private KakaoUserInfoResponse requestKakaoUserInfo(String accessToken) {

        KakaoUserInfoResponse userInfo = webClient.get()
                .uri(kakaoProps.getUserInfoUri())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .map(body -> new BusinessException(
                                        ErrorCode.OAUTH_LOGIN_FAILED,
                                        "카카오 사용자 정보 조회 실패: " + body
                                ))
                )
                .bodyToMono(KakaoUserInfoResponse.class)
                .block();

        if (userInfo == null || userInfo.getId() == null) {
            throw new BusinessException(ErrorCode.OAUTH_LOGIN_FAILED, "카카오 사용자 정보 조회에 실패했습니다.");
        }

        return userInfo;
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

    <p style="font-size:12px; color:#aaa; text-align:center; margin:0%%;">
      본 메일은 발신 전용입니다.<br/>
      © 2025 OPU. All rights reserved.
    </p>

  </div>
</body>
</html>
""".formatted(nickname, verifyUrl);
    }

    private String buildPasswordResetHtml(String nickname, String resetUrl) {
        return """
<html>
<body style="margin:0; padding:0; background:#f8f9fc; font-family: 'Apple SD Gothic Neo', 'Noto Sans KR', sans-serif;">
  <div style="max-width:480px; margin:40px auto; background:#fff; border-radius:12px; padding:32px 24px;
              box-shadow:0 4px 12px rgba(0,0,0,0.06);">

    <h2 style="margin:0 0 8px; font-size:22px; color:#1A1C1F; text-align:center;">
      비밀번호 재설정 안내
    </h2>

    <p style="font-size:15px; color:#555; text-align:center; margin-bottom:24px; line-height:1.5;">
      <span style="font-weight:700; color:#B8DD7C;">%s</span> 님, 비밀번호 재설정 요청이 접수되었습니다.<br/>
      아래 버튼을 눌러 새 비밀번호를 설정해주세요.
    </p>

    <a href="%s" target="_blank"
       style="display:block; width:100%%; background:#B8DD7C; color:#fff;
              text-decoration:none; padding:14px 0; border-radius:8px;
              font-size:16px; font-weight:600; text-align:center;">
      비밀번호 재설정 페이지로 이동
    </a>

    <p style="font-size:12px; color:#999; text-align:center; margin-top:16px;">
      만약 본인이 요청하지 않았다면, 이 메일은 무시하셔도 됩니다.
    </p>

    <hr style="border:none; border-top:1px solid #eee; margin:24px 0;" />

    <p style="font-size:12px; color:#aaa; text-align:center; margin:0;">
      본 메일은 발신 전용입니다.<br/>
      © 2025 OPU. All rights reserved.
    </p>

  </div>
</body>
</html>
""".formatted(nickname, resetUrl);
    }
}