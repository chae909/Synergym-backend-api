package org.synergym.backendapi.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.synergym.backendapi.entity.Role;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key secretKey;
    private final long expirationMs;

    // application.yml 에서 값을 주입받음
    public JwtUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.access-token-validity-in-seconds}") long expirationSeconds) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationSeconds * 1000; // Convert seconds to milliseconds
    }

    /**
     * JWT 토큰 생성
     * @param email 사용자의 이메일
     * @param role 사용자의 역할
     * @return 생성된 JWT 문자열
     */
    public String generateToken(String email, Role role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .claim("email", email)
                .claim("role", role.name())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰에서 Claims 정보 추출
     * @param token JWT 토큰
     * @return Claims 객체
     */
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 토큰에서 이메일 추출
     */
    public String getEmail(String token) {
        return getClaims(token).get("email", String.class);
    }

    /**
     * 토큰에서 역할 추출
     */
    public Role getRole(String token) {
        String roleString = getClaims(token).get("role", String.class);
        return Role.valueOf(roleString);
    }

    /**
     * 토큰 만료 여부 확인
     */
    public boolean isExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }
}
