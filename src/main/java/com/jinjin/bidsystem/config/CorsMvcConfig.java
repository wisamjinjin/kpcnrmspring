
/* 주요 클래스 설명 */
/* - CorsMvcConfig:
       Spring MVC에서 CORS(Cross-Origin Resource Sharing)를 설정하는 클래스
        Spring Security가 더 상위 레벨에서 작동하므로, Spring Security의 설정이 있다면 Spring MVC 설정은 생략해도 됨.
        Spring MVC는 보안 필터를 통과한 요청만 처리,
        Spring MVC 설정이 필요한 세부적인 요구사항(예: 컨트롤러별 CORS 정책)이 있을 경우에만 추가 설정
*/
       
package com.jinjin.bidsystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

    @Configuration
    public class CorsMvcConfig implements WebMvcConfigurer {

        //example
        // @Override
        // public void addCorsMappings(CorsRegistry registry) {
        //     // 공개 API에 대해 모든 도메인 허용
        //     registry.addMapping("/api/public/**")
        //             .allowedOrigins("*")
        //             .allowedMethods("GET", "POST")
        //             .maxAge(3600);
    
        //     // 민감한 API는 특정 도메인만 허용
        //     registry.addMapping("/api/private/**")
        //             .allowedOrigins("http://trusted-domain.com")
        //             .allowedMethods("GET")
        //             .allowCredentials(true)
        //             .maxAge(3600);
        // }
    }
