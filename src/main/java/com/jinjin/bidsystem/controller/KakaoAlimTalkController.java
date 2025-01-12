package com.jinjin.bidsystem.controller;

import com.jinjin.bidsystem.service.AlimtalkService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// 카카오 알림톡 관련 요청을 처리하는 컨트롤러 클래스
@RestController
@RequestMapping("/api/sendkakao") // API 엔드포인트 기본 경로 설정
public class KakaoAlimTalkController {

    // AlimtalkService 주입
    @Autowired
    private AlimtalkService alimtalkService;

    // 카카오 알림톡 메시지를 전송하는 API 엔드포인트
    @PostMapping("/send-kakao-message")
    public ResponseEntity<Map<String, Object>> sendKakaoMessage(@RequestBody Map<String, Object> request) {
        // AlimtalkService를 사용해 액세스 토큰을 가져옴옴.
        String ACCESS_TOKEN = alimtalkService.getAccessToken();
        
        // 클라이언트 요청에서 matchNumber를 추출합니다.
        String matchNumber = (String) request.get("matchNumber");
        
        // AlimtalkService를 호출하여 알림톡 메시지를 전송하고 결과를 받음.
        Map<String, Object> result = alimtalkService.sendAlimtalkByMatch(matchNumber, ACCESS_TOKEN);
        
        // 결과를 ResponseEntity로 반환.
        return ResponseEntity.ok(result);
    }
}
