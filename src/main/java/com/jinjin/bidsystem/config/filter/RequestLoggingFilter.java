/* 주요 클래스 설명 */
/* - RequestLoggingFilter: 디버깅 용 
       Spring Web의 `OncePerRequestFilter`를 확장한 클래스
       HTTP 요청(Request)와 응답(Response)의 세부 정보를 로깅 */

/* - 주요 메서드:
       . doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain):
         - HTTP 요청과 응답 데이터를 처리하는 메인 메서드
         - 요청 메서드, URI, 요청 본문, 응답 상태 코드를 로깅 */

/* - 동작 과정:
       1. 요청 래핑(ContentCachingRequestWrapper):
          - HTTP 요청을 읽을 수 있도록 래핑
          - 요청 본문을 로깅에 사용할 수 있도록 지원
       2. 요청 메서드, URI, 파라미터 로깅
       3. FilterChain 실행:
          - 다음 필터로 요청을 전달
       4. 요청 본문(Content)을 읽고 UTF-8로 디코딩하여 로깅
       5. 응답 상태 코드와 요청 URI 로깅 */

/* - 주요 로깅 포인트:
       1. Incoming Request:
          - 요청 메서드, URI, 파라미터
       2. Request Body:
          - 요청 본문(POST, PUT 등에서 유용)
       3. Request Ended:
          - 응답 상태 코드 및 요청 URI */

/* - 활용 목적:
       . 디버깅 및 모니터링:
         - 요청 및 응답 데이터를 기록하여 문제를 파악
       . 보안 및 감사:
         - 특정 요청의 데이터 흐름 추적
       . 개발 중 요청-응답 분석 */

/* - 주의 사항:
       . 요청 본문이 대규모 데이터일 경우 성능 저하 가능
       . 민감한 데이터가 포함될 수 있으므로 로깅 필터 적용 범위 주의 */


package com.jinjin.bidsystem.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);

        logger.info("\n\n--------------------- Incoming request: "
                + request.getMethod()
                + " "
                + request.getRequestURI()
                + " Params: "
                + request + "\n");

        filterChain.doFilter(wrappedRequest, response);

        byte[] content = wrappedRequest.getContentAsByteArray();
        
        if (content.length > 0) {
            String requestBody = new String(content, StandardCharsets.UTF_8);
            logger.info("\n\n--------------------- Request Body:" + " "
                    + requestBody);
        }

        logger.info("\n\n================ Request ended:" + " "
                + response.getStatus() + " "
                + request.getRequestURI());
    }
}

