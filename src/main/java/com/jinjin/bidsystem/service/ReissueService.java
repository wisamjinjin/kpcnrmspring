
package com.jinjin.bidsystem.service;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.jinjin.bidsystem.config.configProperties.JwtProperties;
import com.jinjin.bidsystem.repository.RefreshRepository;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/* access token 만료 후 refresh token이 유효한 경우 access token 재발금
 * access token 재발급시 refresh token도 재발급하는 경우 "refresh token 재발급 용" comment를 풀면됨
 */

/* 주요 메서드 설명 */
/* 1. ReissueService 생성자:
      - JWTUtil, RefreshRepository, JwtProperties를 초기화
      - Access Token 및 Refresh Token의 만료 시간과 쿠키 만료 시간을 설정 */

/* 2. reissueAccessToken(Map<String, Object> requestBody, HttpServletRequest request, HttpServletResponse response):
      - Access Token 재발급을 처리하는 메서드
      - 주요 기능:
        1) HTTP 요청의 쿠키 배열에서 "Refresh" 토큰을 검색
        2) Refresh Token 유효성 검사:
            - 토큰의 만료 여부 확인
            - 토큰이 "Refresh" 카테고리인지 확인
            - Refresh Token이 DB에 존재하는지 확인
        3) 새 Access Token 생성
            - `createJwt` 메서드를 사용하여 새로운 Access Token을 발급
        4) 응답 헤더 및 쿠키 설정:
            - 새 Access Token을 Authorization 헤더에 추가
            - Refresh Token을 쿠키로 재설정
        5) 응답 본문에 성공 메시지를 포함하여 반환
        6) Refresh Token 재발급 활성화 시:
            - 기존 Refresh Token을 삭제하고 새 Refresh Token을 발급
            - 새 Refresh Token을 DB에 저장하고 쿠키에 설정 */

@Service
public class ReissueService {


    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;      //refresh token 재발급 용
    private final int cookieMaxAge;

    public ReissueService(
        JWTUtil jwtUtil,
        RefreshRepository refreshRepository,
        JwtProperties jwtProperties)
        {
            this.jwtUtil = jwtUtil;
            this.refreshRepository = refreshRepository;
            this.accessTokenExpiration = jwtProperties.getAccessTokenExpiration();
            this.refreshTokenExpiration = jwtProperties.getRefreshTokenExpiration();
            this.cookieMaxAge = jwtProperties.getCookieMaxAge();
        }

    public ResponseEntity<?> reissueAccessToken(Map<String, Object> requestBody, HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> errorResponse = new HashMap<>();

        // 쿠키 배열 가져오기
        Cookie[] cookies = request.getCookies();
        String refreshToken = null;

        // 쿠키 배열에서 "Refresh" 값 찾기
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Refresh".equals(cookie.getName())) {       //대문자 Refresh
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        // "Refresh" 쿠키 값이 없는 경우 처리
        if (refreshToken == null || refreshToken.isEmpty()) {
            errorResponse.put("error_code", "REFRESH_INVALID");
            errorResponse.put("message", "Refresh token is null.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        // Refresh 토큰 유효성 검사
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            errorResponse.put("error_code", "REFRESH_INVALID");
            errorResponse.put("message", "Refresh token is expired.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        // 토큰이 "Refresh"인지 확인
        String category = jwtUtil.getCategory(refreshToken);
        if (!"Refresh".equals(category)) {
            errorResponse.put("error_code", "REFRESH_INVALID");
            errorResponse.put("message", "Refresh token is invalid.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        // DB에 토큰 존재 여부 확인
        boolean isExist = refreshRepository.existsByRefresh(refreshToken);
        if (!isExist) {
            errorResponse.put("error_code", "REFRESH_INVALID");
            errorResponse.put("message", "Refresh token not found in DB.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        // 새로운 JWT 발급
        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);
        String newAccess = jwtUtil.createJwt("Authorization", username, role, accessTokenExpiration);
        // String newRefresh = jwtUtil.createJwt("Refresh", username, role, refreshTokenExpiration);         //refresh token 재발급 용

        // Db Refresh 토큰을 삭제하고 새로 갱신
        // refreshRepository.deleteByRefresh(refreshToken);      //refresh token 재발급 용
        // jwtUtil.addRefreshEntity(username, newRefresh, refreshTokenExpiration);       //refresh token 재발급 용

        // 응답 설정
        response.addHeader("Authorization", "Bearer " + newAccess);
        // response.addCookie(jwtUtil.createCookie("Refresh", newRefresh, cookieMaxAge));        //refresh token 재발급 용
        response.addCookie(jwtUtil.createCookie("Refresh", refreshToken, cookieMaxAge));

        response.setContentType("application/json");

        
        Map<String, String> reponseBody = new HashMap<>();
        // reponseBody.put("refresh", newRefresh);  // 응답에 새로운 Refresh 토큰 포함 : 현재는 cookie에 refresh를 보내므로 필요없음
        reponseBody.put("message", "Access Token이 재발급되었습니다.");

        return ResponseEntity.status(HttpStatus.OK).body(reponseBody);
    }
}

