package com.opu.opube.common.jwt;

import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtEmailTokenProvider {

    private final Key key;
    private final long emailVerifyExpirationMillis;
    private final long passwordResetExpirationMillis;

    public JwtEmailTokenProvider(
            @Value("${app.jwt.email.secret}") String secret,
            @Value("${app.jwt.email.expiration-minutes}") long emailExpMinutes,
            @Value("${app.jwt.password-reset.expiration-minutes}") long resetExpMinutes
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.emailVerifyExpirationMillis = emailExpMinutes * 60 * 1000;
        this.passwordResetExpirationMillis = resetExpMinutes * 60 * 1000;
    }

    /** 이메일 인증용 토큰 생성 */
    public String createEmailVerifyToken(Long memberId) {
        return createToken(memberId, "email", emailVerifyExpirationMillis);
    }

    /** 비밀번호 재설정용 토큰 생성 */
    public String createPasswordResetToken(Long memberId) {
        return createToken(memberId, "password-reset", passwordResetExpirationMillis);
    }

    /** 공통 토큰 생성 로직 */
    private String createToken(Long memberId, String type, long expirationMillis) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(now)
                .setExpiration(exp)
                .claim("typ", type)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** 토큰에서 회원 ID 추출 */
    public Long parseMemberIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return Long.valueOf(claims.getSubject());
    }

    /** 토큰 타입 파악 */
    public String getTokenType(String token) {
        Claims claims = parseClaims(token);
        return claims.get("typ", String.class);
    }

    public boolean isEmailVerifyToken(String token) {
        return "email".equals(getTokenType(token));
    }

    public boolean isPasswordResetToken(String token) {
        return "password-reset".equals(getTokenType(token));
    }

    public Date getIssuedAt(String token) {
        Claims claims = parseClaims(token);
        return claims.getIssuedAt();
    }

    /** 공통 Claims 파서 */
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_JWT);
        }
    }
}