package com.jinjin.bidsystem.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/*
 * SPA(Single Page Application) 컨트롤러 클래스
 * Vue 클라이언트를 빌드한 후 모든 요청을 index.html로 포워딩하여 SPA가 동작하도록 설정
 */

@Controller
public class SpaController {

    // 로깅을 위한 Logger 인스턴스 생성
    private static final Logger logger = LoggerFactory.getLogger(SpaController.class);

    /*
     * HomeController 클래스
     * 특정 경로를 처리하고 index.html로 모든 요청을 포워딩 : 경로에 .이 포함된 예 .css, .pgn .js등등
     * 경로에 '.'이 없는 모든 요청을 처리합니다.
     */

    @Controller
    public class HomeController {

        private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

        /*
         * Get 요청을 처리하는 메서드
         * 경로에 '.'이 포함되지 않은 모든 요청을 index.html로 포워딩.
         * 이 설정은 SPA에서 클라이언트 라우팅을 지원하기 위해 필요.
         */
        
        @GetMapping("/{path:[^\\.]*}")
        public String home() {
            logger.info("SPA 요청 처리 - index.html로 포워딩");
            return "forward:/index.html"; // 요청을 index.html로 포워딩
        }
    }
}
