package com.Iot7_1team.pos_web.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "YourSuperSecretKeyForJwtTokenYourSuperSecretKey"; // ✅ 환경 변수로 관리 권장
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1시간

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // ✅ 토큰 생성 (businessId 포함)
    public String generateToken(String userEmail, Long businessId) {
        return Jwts.builder()
                .setSubject(userEmail)
                .addClaims(Map.of("businessId", businessId)) // ✅ businessId 추가
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ 토큰에서 사용자 이메일 추출
    public String extractUserEmail(String token) {
        return getClaims(token).getBody().getSubject();
    }

    // ✅ 토큰에서 businessId 추출
    public Long extractBusinessId(String token) {
        return getClaims(token).getBody().get("businessId", Long.class);
    }

    // ✅ 토큰 유효성 검증
    public boolean validateToken(String token) {
        return extractUserEmail(token) != null && !isTokenExpired(token);
    }

    // ✅ 토큰 만료 여부 확인
    private boolean isTokenExpired(String token) {
        return getClaims(token).getBody().getExpiration().before(new Date());
    }

    // ✅ 클레임 추출
    private Jws<Claims> getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }
}
