package com.opu.opube.feature.auth.command.application.controller;

import com.opu.opube.common.dto.ApiResponse;
import com.opu.opube.exception.BusinessException;
import com.opu.opube.feature.auth.command.application.dto.request.*;
import com.opu.opube.feature.auth.command.application.dto.response.EmailVerifyStatusResponse;
import com.opu.opube.feature.auth.command.application.dto.response.KakaoLoginResponse;
import com.opu.opube.feature.auth.command.application.dto.response.RegisterResponse;
import com.opu.opube.feature.auth.command.application.dto.response.TokenResponse;
import com.opu.opube.feature.auth.command.application.security.MemberPrincipal;
import com.opu.opube.feature.auth.command.application.service.AuthCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import com.opu.opube.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Tag(
        name = "Auth",
        description = "회원가입, 로그인, 이메일 인증, 비밀번호 재설정, 소셜 로그인 등 인증 관련 API"
)
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthCommandService authCommandService;
    private final AppProperties appProperties;

    private static final String SIGNUP_EMAIL_CONFIRMED_PATH = "/signup/email-confirmed";
    private static final String SIGNUP_EMAIL_FAILED_PATH    = "/signup/email-failed";

    private static final String REASON_PARAM   = "reason";
    private static final String REASON_EXPIRED = "expired";
    private static final String REASON_INVALID = "invalid";


    // 회원가입 이메일 발송
    @Operation(
            summary = "회원가입 (이메일 기반)",
            description = """
                    이메일/비밀번호로 로컬 계정을 생성합니다.
                    회원가입 완료 후 인증 메일이 발송되며, 이메일 인증이 완료되어야 로그인할 수 있습니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "회원가입 이메일 발송 성공",
                    content = @Content(schema = @Schema(implementation = RegisterResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "이미 가입된 이메일",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "요청 값 검증 실패",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @RequestBody @Valid RegisterRequest req) {

        Long id = authCommandService.register(req, appProperties.getBackendBaseUrl());

        RegisterResponse response = RegisterResponse.builder()
                .memberId(id)
                .message("회원가입 성공. 이메일을 확인하세요.")
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }



    // 로그인
    @Operation(
            summary = "로그인",
            description = """
                    이메일과 비밀번호로 로그인하여 Access Token / Refresh Token을 발급합니다.
                    - 이메일 인증이 완료된 계정만 로그인 가능합니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "비밀번호 불일치 또는 이메일 미인증, 존재하지 않는 회원",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "요청 값 검증 실패",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody @Valid LoginRequest req) {
        TokenResponse tokens = authCommandService.login(req.getEmail(), req.getPassword());
        return ResponseEntity.ok(ApiResponse.success(tokens));
    }


    // 이메일 인증
    @Operation(
            summary = "이메일 인증 링크 처리",
            description = """
                    이메일에 포함된 인증 토큰을 검증하고 결과에 따라 프론트엔드로 리다이렉트합니다.

                    - 성공: 302 Redirect → `{frontendBaseUrl}/signup/email-confirmed`
                    - 실패:
                      - 토큰 만료: `{frontendBaseUrl}/signup/email-failed?reason=expired`
                      - 잘못된/변조된 토큰, 존재하지 않는 회원 등: `{frontendBaseUrl}/signup/email-failed?reason=invalid`
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "302",
                    description = "프론트엔드 이메일 인증 결과 페이지로 리다이렉트 (응답 본문 없음)"
            )
    })

    @GetMapping("/verify")
    public void verify(
            @RequestParam("token") String token,
            HttpServletResponse response
    ) throws IOException {

        try {
            authCommandService.verifyEmail(token);

            String redirectUrl = UriComponentsBuilder
                    .fromHttpUrl(appProperties.getFrontendBaseUrl())
                    .path(SIGNUP_EMAIL_CONFIRMED_PATH)
                    .build()
                    .toUriString();

            response.sendRedirect(redirectUrl);

        } catch (BusinessException ex) {

            String reason = switch (ex.getErrorCode()) {
                case EMAIL_VERIFY_TOKEN_EXPIRED -> REASON_EXPIRED;
                case INVALID_EMAIL_VERIFY_TOKEN, MEMBER_NOT_FOUND -> REASON_INVALID;
                default -> REASON_INVALID;
            };

            String redirectUrl = UriComponentsBuilder
                    .fromHttpUrl(appProperties.getFrontendBaseUrl())
                    .path(SIGNUP_EMAIL_FAILED_PATH)
                    .queryParam(REASON_PARAM, reason)
                    .build()
                    .toUriString();

            response.sendRedirect(redirectUrl);
        }
    }

    // 토큰 재발급
    @Operation(
            summary = "Token 재발급",
            description = """
                    유효한 Refresh Token을 사용해 새로운 Access Token / Refresh Token을 발급합니다.
                    서버에 저장된 Refresh Token과 일치해야 하며, 만료되지 않아야 합니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "토큰 재발급 성공",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "유효하지 않거나 저장되지 않은 Refresh Token",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@RequestBody @Valid RefreshTokenRequest req) {
        TokenResponse tokenResponse = authCommandService.refreshToken(req);
        return ResponseEntity.ok(ApiResponse.success(tokenResponse));
    }

    //카카오 로그인
    @Operation(
            summary = "카카오 로그인",
            description = """
                    카카오 인가 코드로 로그인을 수행합니다.
                    - 기존에 가입된 카카오 계정이면 JWT 토큰이 포함된 응답을 반환합니다.
                    - 가입 이력이 없으면 프론트에서 추가 정보 입력 후 /kakao/register를 호출해야 합니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "카카오 로그인 처리 결과",
                    content = @Content(schema = @Schema(implementation = KakaoLoginResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "카카오 OAuth 연동 실패",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @GetMapping("/kakao/login")
    public ResponseEntity<ApiResponse<KakaoLoginResponse>> kakaoLogin(
            @RequestParam String code
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(authCommandService.kakaoLogin(code))
        );
    }

    // 카카오 회원가입
    @Operation(
            summary = "카카오 회원가입",
            description = """
                    카카오 로그인 이후 추가 정보를 받아 신규 회원을 생성하고 JWT 토큰을 발급합니다.
                    - 이미 해당 providerId로 가입된 계정이 있으면 오류를 반환합니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "카카오 회원가입 + 로그인 성공",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "이미 가입된 카카오 계정",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/kakao/register")
    public ResponseEntity<ApiResponse<TokenResponse>> kakaoRegister(
            @RequestBody @Valid KakaoRegisterRequest req
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(authCommandService.kakaoRegister(req))
        );
    }

    // 비밀번호 재설정 이메일 요청
    @Operation(
            summary = "비밀번호 재설정 이메일 요청",
            description = """
                    이메일 주소를 입력하면 비밀번호 재설정 링크를 해당 이메일로 전송합니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "요청 접수",
                    content = @Content(schema = @Schema(implementation = Void.class))
            )
    })
    @PostMapping("/password/reset-request")
    public ResponseEntity<ApiResponse<Void>> requestPasswordReset(
            @RequestBody PasswordResetRequest req
    ) {
        authCommandService.requestPasswordReset(req, appProperties.getFrontendBaseUrl());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 비밀번호 재설정
    @Operation(
            summary = "비밀번호 재설정",
            description = """
                    이메일로 전달된 비밀번호 재설정 토큰과 새 비밀번호를 이용해 비밀번호를 재설정합니다.
                    - 토큰이 최신 발급 토큰이 아닌 경우 또는 만료된 경우 재설정에 실패합니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "비밀번호 재설정 성공",
                    content = @Content(schema = @Schema(implementation = Void.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "유효하지 않거나 만료된 비밀번호 재설정 토큰",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/password/reset")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestBody PasswordResetConfirmRequest req
    ) {
        authCommandService.resetPassword(req);
        return ResponseEntity.ok(ApiResponse.success(null));
    }



    @Operation(
            summary = "이메일 인증 메일 재발송",
            description = """
                    이미 가입했지만 이메일 인증을 완료하지 않은 계정에 대해 인증 메일을 다시 발송합니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "인증 메일 재발송 요청 성공",
                    content = @Content(schema = @Schema(implementation = Void.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "가입되지 않은 이메일",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "이미 이메일 인증이 완료된 계정",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/verify/resend")
    public ResponseEntity<ApiResponse<Void>> resendVerificationEmail(
            @RequestBody ResendVerificationEmailRequest req
    ) {
        authCommandService.resendVerificationEmail(req.getEmail(), appProperties.getBackendBaseUrl());
        return ResponseEntity.ok(ApiResponse.success(null));
    }


    @Operation(
            summary = "비밀번호 변경",
            description = """
                    로그인된 사용자가 기존 비밀번호를 검증한 후 새 비밀번호로 변경합니다.
                    비밀번호 변경 시 기존 Refresh Token은 모두 무효화됩니다.
                    """,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "비밀번호 변경 성공",
                    content = @Content(schema = @Schema(implementation = Void.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "기존 비밀번호 불일치 또는 인증 실패",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/password/change")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestBody @Valid ChangePasswordRequest req,
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        Long memberId = principal.getMemberId();
        authCommandService.changePassword(memberId, req);
        return ResponseEntity.ok(ApiResponse.success(null));
    }


    @Operation(
            summary = "현재 비밀번호 확인",
            description = """
                    마이페이지 등에서 현재 비밀번호가 올바른지 확인할 때 사용합니다.
                    """,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "비밀번호 일치 (내용 없는 성공 응답)",
                    content = @Content(schema = @Schema(implementation = Void.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "비밀번호 불일치 또는 인증 실패",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/password/check")
    public ResponseEntity<ApiResponse<Void>> checkPassword(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestBody @Valid PasswordCheckRequest req
    ) {
        authCommandService.checkCurrentPassword(principal.getMemberId(), req.getPassword());
        return ResponseEntity.ok(ApiResponse.success(null));
    }



    @Operation(
            summary = "로그아웃",
            description = """
                    서버에 저장된 Refresh Token을 제거하여 로그아웃 처리합니다.
                    Access Token은 클라이언트 측에서 제거해야 합니다.
                    """,
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그아웃 성공",
                    content = @Content(schema = @Schema(implementation = Void.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        authCommandService.logout(principal.getMemberId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(
            summary = "이메일 인증 상태 조회",
            description = """
                이메일 기반 계정의 이메일 인증 여부를 조회합니다.
                true = 인증 완료, false = 미인증
                """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = EmailVerifyStatusResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "가입되지 않은 이메일",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @GetMapping("/verify/status")
    public ResponseEntity<ApiResponse<EmailVerifyStatusResponse>> getEmailVerifyStatus(
            @RequestParam String email
    ) {
        boolean verified = authCommandService.isEmailVerified(email);

        EmailVerifyStatusResponse body = EmailVerifyStatusResponse.builder()
                .verified(verified)
                .build();

        return ResponseEntity.ok(ApiResponse.success(body));
    }
}