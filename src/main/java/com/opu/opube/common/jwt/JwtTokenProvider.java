package com.opu.opube.common.jwt;

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
            @Value("${app.jwt.refresh-exp-sec:1209600}") long refreshExpSec) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenMillis = accessExpSec * 1000;
        this.refreshTokenMillis = refreshExpSec * 1000;
    }

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

    public Long parseMemberId(String token) {
        Claims c = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return Long.valueOf(c.getSubject());
    }

    public long getAccessExpirationSeconds() {
        return accessTokenMillis / 1000;
    }

    public long getRefreshExpirationSeconds() {
        return refreshTokenMillis / 1000;
    }
}