package com.jinjin.bidsystem.controller;
import com.jinjin.bidsystem.service.SmsService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/sendsms")
public class SmsController {
@Autowired
    private SmsService smsService;
    
    // 인증 코드 전송
    @PostMapping("/send-auth-code")
    public ResponseEntity<Map<String, Object>> sendVerificationMessage(@RequestBody Map<String, Object> request) { 
        Map<String, Object> results = smsService.sendVerificationMessage(request);
        return ResponseEntity.ok(results); 
    }
    // 인증 코드 검증
    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, Object>> verifyCode(@RequestBody Map<String, Object> request) {
        Map<String, Object> results = smsService.verifyCode(request);
        return ResponseEntity.ok(results); 
    }
}
