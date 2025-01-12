/* 주요 클래스 설명 */
/* - JWTFilter:
       Spring Security의 OncePerRequestFilter를 확장한 클래스
       각 HTTP 요청에서 JWT(Access Token)를 검증하여 사용자 인증 상태를 설정 */

/* - 주요 필드:
       . jwtUtil: JWT 유틸리티 클래스, 토큰 검증 및 정보 추출

/* - 주요 메서드:
       . shouldNotFilter(HttpServletRequest request):
         - 특정 URI 경로를 제외하여 필터를 실행하지 않음
         - 로그인, 회원가입, 정적 파일 요청 등

       . doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain):
         - 요청 헤더에서 Access Token을 추출하여 유효성을 검증
         - 토큰에서 사용자 정보(username, role) 추출
         - Spring Security 인증 객체 생성 및 컨텍스트에 설정
         - 예외 상황(토큰 만료, 유효하지 않음 등) 시 적절한 HTTP 응답 반환 */

/* - 동작 과정:
       1. 특정 URI가 제외 대상인지 확인 (`shouldNotFilter`)
       2. Access Token 검증:
          - null 확인 및 "Bearer " 접두어 
          - 만료 여부, 카테고리 확인
       3. 토큰에서 사용자 정보 추출:
          - username, role
       4. Spring Security 인증 객체 생성:
          - `CustomUserDetails` 및 `UsernamePasswordAuthenticationToken` 사용
          - `SecurityContextHolder`에 설정 */

/* - 주요 예외 처리:
       . Access Token 누락, 만료, 유효하지 않음 등에 대해
         HTTP 401 상태 코드와 JSON 응답 반환 */

/* - 활용 목적:
       . JWT 기반 인증을 통해 각 요청에 대해 사용자 식별 및 인증 */


package com.jinjin.bidsystem.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jinjin.bidsystem.dto.CustomUserDetails;
import com.jinjin.bidsystem.entity.UserEntity;
import com.jinjin.bidsystem.service.JWTUtil;

import io.jsonwebtoken.ExpiredJwtException;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * JWT 필터 클래스 - 각 요청에 대해 JWT 토큰을 검증하여 인증 상태를 관리
 */
@Component
public class JWTFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JWTFilter.class);
    private final JWTUtil jwtUtil;

    // JWTFilter 제외 대상 URI Set
    private static final Set<String> EXCLUDED_URIS = new HashSet<>(Arrays.asList(
            "/login", "/register",

            //client에서 결제요청을 하기위해 redirect하거나 결제 후 return 
            "/api/pgstart", "/api/pgreturn",
            "/api/pgstart-mobile", "/api/pgreturn-mobile",
            //결제 종료 후 return용 
           "/bidseats", 
            
            //vue build후 수행하는 client url 및 asset등 경로
            "/WEB-INF/views/",
            "/static/", "/index.html", "/assets/", "/css/", "/icons/", "/images/",

            //access token만료 후 재발급급
           "/reissue-access-token",

            // 사용자 등록시 전화번호, 이메일 중복 확인 및 인증을 위해 아래 포함
            "/api/user/get-email-count", "/api/user/get-telno-count", 
            "/api/sendsms/send-auth-code", "/api/sendsms/verify-code"
    ));

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        boolean excluded = EXCLUDED_URIS.stream().anyMatch(requestURI::startsWith);
    
        if (excluded) {
            logger.info("\n------   Jwt filter is excluded for URL: {}", request.getRequestURL());
            return true; // 제외 대상인 경우 true 반환
        }
    
        return false; // 제외 대상이 아닌 경우 false 반환
    }
    

    /**
     * JWT 토큰 필터 처리 메서드
     * 각 요청에 대해 JWT 토큰의 유효성을 검증하고, 인증 정보를 설정
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Authorization 헤더에서 JWT 토큰 추출
        String accessToken = request.getHeader("Authorization");
        if (accessToken == null || !accessToken.startsWith("Bearer ")) {
            // 유효하지 않은 Authorization 헤더 처리
            logger.warn("\n------   Invalid Authorization header - Request URL: {}", request.getRequestURL());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"errorCode\": \"INVALID_HEADER\", \"message\": \"Missing or invalid Authorization header.\"}");
            return;
        }

        // "Bearer " 접두사를 제거하여 순수 토큰 추출
        String token = accessToken.substring(7);

        try {
            // 토큰의 만료 여부 확인
            jwtUtil.isExpired(token);
        } catch (ExpiredJwtException e) {
            // 만료된 토큰 처리
            logger.info("\n------   Access token expired - Request URL: {}", request.getRequestURL());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"errorCode\": \"ACCESS_EXPIRED\", \"message\": \"Access token expired.\"}");
            return;
        } catch (Exception e) {
            // 토큰 유효성 검증 오류 처리
            logger.error("\n------   Error validating token: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"errorCode\": \"TOKEN_VALIDATION_ERROR\", \"message\": \"Error validating token.\"}");
            return;
        }

        // 토큰 카테고리 확인
        String category = jwtUtil.getCategory(token);
        if (!"Authorization".equals(category)) {
            // 잘못된 토큰 카테고리 처리
            logger.warn("\n------   Invalid access token - Request URL: {}", request.getRequestURL());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"errorCode\": \"ACCESS_INVALID\", \"message\": \"Invalid access token.\"}");
            return;
        }

        // 토큰에서 사용자 정보 추출
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        // 사용자 정보로 UserEntity 객체 생성
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setRole(role);

        // 사용자 정보를 기반으로 CustomUserDetails 객체 생성
        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

        // 인증 토큰 생성 및 SecurityContext에 설정
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 다음 필터 체인으로 진행
        filterChain.doFilter(request, response);
    }
}


