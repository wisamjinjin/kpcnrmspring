package com.jinjin.bidsystem.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jinjin.bidsystem.dto.UserRegistrationDto;
import com.jinjin.bidsystem.service.RegisterService;

// 회원 등록 요청을 처리하는 컨트롤러 클래스
@Controller
@ResponseBody // 컨트롤러가 반환하는 값을 JSON 형식으로 변환
public class RegisterController {

    // 회원 등록을 처리하는 서비스
    private RegisterService registerService;

    // RegisterService를 생성자 주입 방식으로 설정
    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    // 회원 등록 처리 API 엔드포인트
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerProcess(UserRegistrationDto registerDTO) {
        // RegisterService를 호출하여 회원 등록 처리 결과를 가져옴.
        Map<String, Object> result = registerService.registerProcess(registerDTO);
        
        // 결과를 HTTP 상태 코드 200과 함께 반환.
        return ResponseEntity.ok(result);
    }
}
