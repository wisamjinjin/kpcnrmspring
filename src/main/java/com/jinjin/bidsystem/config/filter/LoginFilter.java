/* 주요 클래스 설명 */
/* - LoginFilter:
       Spring Security의 `UsernamePasswordAuthenticationFilter`를 확장한 클래스
       사용자의 로그인 요청을 처리하며 JWT(Access 및 Refresh Token)를 생성 */

/* - 주요 필드:
       . authManager: Spring Security 인증 관리자
       . refreshRepository: Refresh Token 저장소
       . jwtUtil: JWT 유틸리티 클래스, 토큰,쿠키 생성 및 관리
       . accessTokenExpiration: Access Token 만료 시간
       . refreshTokenExpiration: Refresh Token 만료 시간
       . cookieMaxAge: Refresh Token의 쿠키 만료 시간 */

/* - 주요 메서드:
       . attemptAuthentication(HttpServletRequest request, HttpServletResponse response):
         - 사용자 인증을 시도하는 메서드
         - 사용자명(username)과 비밀번호(password)를 기반으로 인증 토큰 생성
         - 인증 관리자(authManager)를 통해 인증 처리

       . successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication):
         - 인증 성공 시 호출
         - 사용자 정보와 권한을 기반으로 JWT 생성
         - 기존 Refresh Token 삭제 및 새 Refresh Token 저장
         - Access Token은 응답 헤더, Refresh Token은 쿠키에 포함
         - 사용자 정보(username, role, telno)를 JSON 형식으로 응답

       . unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed):
         - 인증 실패 시 호출
         - HTTP 상태 코드 401 반환 및 오류 메시지 응답 */

/* - 동작 과정:
       1. 로그인 요청 수신 시 `attemptAuthentication` 실행
       2. 인증 성공:
          - `successfulAuthentication`에서 JWT 생성 및 응답
       3. 인증 실패:
          - `unsuccessfulAuthentication`에서 HTTP 401 상태 코드 반환 */

/* - 주요 예외 처리:
       . 잘못된 사용자명 또는 비밀번호 입력 시 인증 실패 처리
       . Refresh Token 처리 시 기존 토큰이 없거나 오류가 발생하면 경고 로그 생성 */

/* - 활용 목적:
       . 사용자 로그인 시 JWT 기반 인증 제공
       . Access Token과 Refresh Token을 통해 상태 유지 */

package com.jinjin.bidsystem.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinjin.bidsystem.config.configProperties.JwtProperties;
import com.jinjin.bidsystem.dto.CustomUserDetails;
import com.jinjin.bidsystem.repository.RefreshRepository;
import com.jinjin.bidsystem.service.JWTUtil;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private static final Logger logger = LoggerFactory.getLogger(LoginFilter.class);

    private final AuthenticationManager authManager;
    private final RefreshRepository refreshRepository;
    private final JWTUtil jwtUtil;
    private final Long accessTokenExpiration;
    private final Long refreshTokenExpiration;
    private final Integer cookieMaxAge;

    public LoginFilter(AuthenticationManager authManager, RefreshRepository refreshRepository, JWTUtil jwtUtil, JwtProperties jwtProperties) {
        this.authManager = authManager;
        this.refreshRepository = refreshRepository;
        this.jwtUtil = jwtUtil;
        this.accessTokenExpiration = jwtProperties.getAccessTokenExpiration();
        this.refreshTokenExpiration = jwtProperties.getRefreshTokenExpiration();
        this.cookieMaxAge = jwtProperties.getCookieMaxAge();
    }

    // 인증 시도 메서드 수정
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        logger.info("\n------   Authentication for request URI: {}, Method: {}", requestUri, method);

        String username = obtainUsername(request);
        String password = obtainPassword(request);
        // 인증 토큰 생성
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

        // 인증 관리자에 의해 인증 시도
        return authManager.authenticate(authToken);
    }

    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // 토큰 생성
        String accessToken = jwtUtil.createJwt("Authorization", username, role, accessTokenExpiration);
        String refreshToken = jwtUtil.createJwt("Refresh", username, role, refreshTokenExpiration);

        // 기존 Refresh 토큰 삭제 후 new refresh 저장
        String currentRefresh = jwtUtil.getRefreshTokenFromCookies(request, "Refresh");
        if (currentRefresh != null) {
            refreshRepository.deleteByRefresh(currentRefresh);
        } else {
            logger.warn("\n------   LoginFiler No Refresh Token found in cookies.");
        }
        jwtUtil.addRefreshEntity(username, refreshToken, refreshTokenExpiration);

        // 응답 설정
        response.addHeader("Authorization", "Bearer " + accessToken);
        response.addCookie(jwtUtil.createCookie("Refresh", refreshToken, cookieMaxAge));
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 응답 데이터를 Map으로 구성
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("username", username); // 사용자 이름
        responseMap.put("role", role);        // 권한
        responseMap.put("telno", customUserDetails.getTelno()); // 전화번호

        // ObjectMapper로 Map을 JSON 문자열로 변환 후 응답에 작성
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(responseMap);
        logger.info("\n------   LoginFilter success: new acces and refresh are issued for username: {}", username);

        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

// 인증 실패 시 호출되는 메서드 구현
protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
    logger.error("\n------   Authentication failed: {}", failed.getMessage());
    
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType("application/json; charset=UTF-8"); // UTF-8 설정
    response.setCharacterEncoding("UTF-8"); // 응답 인코딩 설정

    String errorMessage;
    String errorCode;

    if (failed instanceof UsernameNotFoundException) {
        // ID가 존재하지 않는 경우
        errorCode = "USER_NOT_FOUND";
        errorMessage = "존재하지 않는 사용자입니다. ID를 확인하세요.";
    } else if (failed instanceof BadCredentialsException) {
        // 암호가 틀린 경우
        errorCode = "INVALID_PASSWORD";
        errorMessage = "암호가 틀렸습니다. 다시 시도하세요.";
    } else {
        // 기타 인증 실패
        errorCode = "AUTH_FAILED";
        errorMessage = "인증에 실패하였습니다. ID와 암호를 확인하세요.";
    }

    // JSON 응답 작성
    response.getWriter().write(String.format("{\"errorCode\": \"%s\", \"message\": \"%s\"}", errorCode, errorMessage));
    response.getWriter().flush();
}

}
