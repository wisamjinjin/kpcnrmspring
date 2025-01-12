/* 주요 클래스 설명 */
/* - CustomLogoutFilter:
       사용자가 로그아웃 요청 시 처리하는 커스텀 필터 클래스
       Refresh 토큰을 DB에서 삭제하고 쿠키에서도 제거하여 로그아웃을 완성 */

/* - 주요 필드:
       . jwtUtil: JWT 유틸리티 클래스, 토큰 검증 및 처리
       . refreshRepository: Refresh 토큰 관리용 데이터베이스 리포지토리  */

/* - 주요 메서드:
       . doFilter(ServletRequest, ServletResponse, FilterChain):
         - HTTP 요청을 CustomLogoutFilter에 맞게 변환 후 처리
       . doFilter(HttpServletRequest, HttpServletResponse, FilterChain):
         - 로그아웃 요청("/logout")을 처리
         - Refresh 토큰 검증 및 삭제
         - 쿠키에 저장된 Refresh 토큰 제거 */

/* - 동작 과정:
       . 로그아웃 요청인지 확인 (URI: `/logout`, 메서드: `POST`)
       . Refresh 토큰 검증:
         - 쿠키에서 Refresh 토큰 추출
         - 유효성 검사 (만료 여부, 카테고리 확인)
         - 데이터베이스에서 존재 여부 확인
       . Refresh 토큰 삭제:
         - 데이터베이스에서 Refresh 토큰 제거
         - 쿠키에서 Refresh 토큰 제거
       . 성공 시 200 OK 상태 코드 반환 */

/* - 주요 예외 처리:
       . Refresh 토큰 누락, 만료, 잘못된 카테고리, DB 미존재 시 HTTP 400 상태 코드 반환 */

package com.jinjin.bidsystem.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;

import com.jinjin.bidsystem.repository.RefreshRepository;
import com.jinjin.bidsystem.service.JWTUtil;

import io.jsonwebtoken.ExpiredJwtException;
import java.io.IOException;


/*
 * logout시 refresh token을 db에서 삭제하고 cookie를 삭제함.
 */

public class CustomLogoutFilter extends GenericFilterBean {
    private static final Logger logger = LoggerFactory.getLogger(CustomLogoutFilter.class);

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public CustomLogoutFilter(JWTUtil jwtUtil, RefreshRepository refreshRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        // Path and method verification
        String requestUri = request.getRequestURI();

        if (!requestUri.matches("/logout")) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestMethod = request.getMethod();

        if (!requestMethod.equals("POST")) {
            logger.info("\n------   During logout Request method is not POST. Passing through filter chain.");
            filterChain.doFilter(request, response);
            return;
        }
        // Get refresh token from cookies
        String refreshToken = jwtUtil.getRefreshTokenFromCookies(request, "Refresh");
        if (refreshToken == null) {
            logger.error("\n------   During logout No cookies found in the request.");
        }

        // Refresh token null check
        if (refreshToken == null) {
            logger.error("\n------   During logout Refresh token is null.");
            response.setContentType("application/json");
            response.getWriter().write("{\"errorCode\": \"REFRESH_INVALID\", \"message\": \"Refresh token is invalid(null) in logout. Please login.\"}");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Expired check
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            logger.info("\n------   During logout Refresh token is expired.");
            response.setContentType("application/json");
            response.getWriter().write("{\"errorCode\": \"REFRESH_INVALID\", \"message\": \"Refresh token is invalid(expired)in logout. Please login.\"}");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Verify token category
        String category = jwtUtil.getCategory(refreshToken);

        if (!category.equals("Refresh")) {
            logger.error("\n------   During logout invalid refresh token category: {}", category);
            response.setContentType("application/json");
            response.getWriter().write("{\"errorCode\": \"REFRESH_INVALID\", \"message\": \"Refresh token is invalid(category)in logout. Please login.\"}");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Check if token exists in DB
        Boolean isExist = refreshRepository.existsByRefresh(refreshToken);

        if (!isExist) {
            logger.error("\n------   During logout refresh token does not exist in the database.");
            response.setContentType("application/json");
            response.getWriter().write("{\"errorCode\": \"REFRESH_INVALID\", \"message\": \"Refresh token is not in DBin logout. Please login.\"}");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Delete refresh token from DB
        refreshRepository.deleteByRefresh(refreshToken);

        // Set refresh token cookie value to null
        Cookie cookie = new Cookie("Refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
