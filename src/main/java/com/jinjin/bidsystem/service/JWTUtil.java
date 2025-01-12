package com.jinjin.bidsystem.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jinjin.bidsystem.config.SecurityConfig;
import com.jinjin.bidsystem.entity.RefreshEntity;
import com.jinjin.bidsystem.repository.RefreshRepository;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* 주요 메서드 설명 */
/* 1. JWTUtil 생성자:
      - JWT 비밀 키를 설정하고 `RefreshRepository`를 초기화
      - 비밀 키는 애플리케이션 속성에서 주입 */

/* 2. getCategory(String token):
      - JWT에서 "category" 클레임 값을 추출 */

/* 3. getUsername(String token):
      - JWT에서 "username" 클레임 값을 추출 */

/* 4. getRole(String token):
      - JWT에서 "role" 클레임 값을 추출 */

/* 5. isExpired(String token):
      - JWT의 만료 날짜를 확인하여 토큰이 만료되었는지 여부를 반환 */

/* 6. createJwt(String category, String username, String role, Long expiredMs):
      - 주어진 클레임과 만료 시간을 기반으로 JWT를 생성
      - 생성된 JWT를 문자열로 반환 */

/* 7. addRefreshEntity(String username, String refresh, Long expiredMs):
      - Refresh 토큰 정보를 `RefreshEntity` 객체로 생성하여 데이터베이스에 저장
      - 만료 시간은 현재 시간에서 설정된 밀리초를 더한 값 */

/* 8. createCookie(String key, String value, int maxAge):
      - 주어진 이름(key)과 값(value)으로 HTTP 전용 쿠키를 생성
      - `maxAge`는 쿠키의 만료 시간을 초 단위로 설정 */
      
/* 9. getRefreshTokenFromCookies(HttpServletRequest request, String cookieName):
      - HTTP 요청의 쿠키 배열에서 지정된 이름의 쿠키 값을 추출
      - 쿠키가 없거나 이름이 일치하지 않을 경우 `null`을 반환 */


@Component
public class JWTUtil {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private SecretKey secretKey;
    private RefreshRepository refreshRepository;

    public JWTUtil(
            @Value("${jwt.secret}") String secret,
            RefreshRepository refreshRepository) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.refreshRepository = refreshRepository;
    }


    public String getCategory(String token) {
      
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }
    
    public String getUsername(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String createJwt(String category, String username, String role, Long expiredMs) {

        return Jwts.builder()
                .claim("category", category)
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public void addRefreshEntity(String username, String refresh, Long expiredMs) {
        Date expirationDate = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(expirationDate);
        refreshRepository.save(refreshEntity);
    }


    public Cookie createCookie(String key, String value, int maxAge) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(maxAge); // 초 단위로 설정
        cookie.setHttpOnly(true);
        // cookie.setSecure(true); // HTTPS 환경에서 사용
        // cookie.setPath("/"); // 필요에 따라 경로 설정
        return cookie;
    }

    public String getRefreshTokenFromCookies(HttpServletRequest request, String cookieName) {
    if (request.getCookies() != null) {
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(cookieName)) {
                return cookie.getValue();
            }
        }
    }
    return null; // 쿠키가 없거나 이름이 일치하지 않으면 null 반환
    }

}