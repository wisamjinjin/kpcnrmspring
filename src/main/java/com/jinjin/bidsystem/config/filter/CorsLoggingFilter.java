
/* 주요 클래스 설명 */
/* - CorsLoggingFilter: 디버깅 용
       HTTP 요청의 CORS 관련 정보를 로깅하는 필터 클래스
       Spring Boot에서 HTTP 요청 처리 체인에 추가됨 */
       
package com.jinjin.bidsystem.config.filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class CorsLoggingFilter extends HttpFilter {

    private static final Logger logger = LoggerFactory.getLogger(CorsLoggingFilter.class);

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        String origin = request.getHeader("Origin");

        if (origin == null) {
            String host = request.getHeader("Host");
            String userAgent = request.getHeader("User-Agent");
            logger.info("\n------   In CorsLoggingFilter.java, Origin null Host: " + host+" "+userAgent+"\n\n");
        }

        // 다음 필터 또는 서블릿 실행
        chain.doFilter(request, response);
    }
}
