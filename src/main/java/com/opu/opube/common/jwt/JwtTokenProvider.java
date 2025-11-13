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
public class JwtTokenProvider {

    private final Key key;
    private final long accessTokenMillis;
    private final long refreshTokenMillis;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-exp-sec:900}") long accessExpSec,
            @Value("${app.jwt.refresh-exp-sec:1209600}") long refreshExpSec
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenMillis = accessExpSec * 1000;
        this.refreshTokenMillis = refreshExpSec * 1000;
    }

    /** 액세스 토큰 생성 */
    public String createAccessToken(Long memberId) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenMillis))
                .claim("typ", "access")
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** 리프레시 토큰 생성 */
    public String createRefreshToken(Long memberId) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenMillis))
                .claim("typ", "refresh")
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** 토큰에서 memberId 추출 */
    public Long parseMemberId(String token) {
        Claims claims = parseClaims(token);
        return Long.valueOf(claims.getSubject());
    }

    /** 액세스 만료시간(초) */
    public long getAccessExpirationSeconds() {
        return accessTokenMillis / 1000;
    }

    /** 리프레시 만료시간(초) */
    public long getRefreshExpirationSeconds() {
        return refreshTokenMillis / 1000;
    }

    /** 토큰 유효성 검증 */
    public void validateToken(String token) {
        // parseClaims 안에서 예외 처리 + BusinessException으로 래핑
        parseClaims(token);
    }

    /** refresh 토큰인지 여부 */
    public boolean isRefreshToken(String token) {
        String typ = getTokenType(token);
        return "refresh".equalsIgnoreCase(typ);
    }

    /** access 토큰인지 여부 */
    public boolean isAccessToken(String token) {
        String typ = getTokenType(token);
        return "access".equalsIgnoreCase(typ);
    }

    /** 토큰 타입 가져오기 */
    public String getTokenType(String token) {
        Claims claims = parseClaims(token);
        return claims.get("typ", String.class);
    }

    /** 토큰 남은 만료 시간(초) */
    public long getRemainingSeconds(String token) {
        Claims claims = parseClaims(token);
        Date exp = claims.getExpiration();
        long diffMillis = exp.getTime() - System.currentTimeMillis();
        return diffMillis > 0 ? diffMillis / 1000 : 0;
    }

    /** Claims 파서 */
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ErrorCode.EXPIRED_JWT);
        } catch (UnsupportedJwtException e) {
            throw new BusinessException(ErrorCode.UNSUPPORTED_JWT);
        } catch (MalformedJwtException | SecurityException e) {
            throw new BusinessException(ErrorCode.INVALID_JWT);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.EMPTY_JWT);
        }
    }
}