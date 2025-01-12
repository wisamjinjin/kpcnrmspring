package com.jinjin.bidsystem.service;
import org.springframework.stereotype.Service;

import com.jinjin.bidsystem.config.configProperties.SmsProperties;
import com.jinjin.bidsystem.service.ExceptionService.*;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;  //테스트 단계에서는 실제로 전화번화로 전송하지 않음.
import net.nurigo.sdk.message.response.SingleMessageSentResponse; //테스트 단계에서는 실제로 전화번화로 전송하지 않음.
import net.nurigo.sdk.message.service.DefaultMessageService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/* 주요 메서드 설명 */
/* 1. SmsService 생성자:
      - `SmsProperties`를 통해 CoolSMS API 설정을 초기화
      - `DefaultMessageService`를 생성하여 SMS 메시지 전송 기능 활성화 */

/* 2. sendVerificationMessage(Map<String, Object> request):
      - 사용자에게 인증 코드를 전송하는 메서드
      - 주요 기능:
        1) 랜덤 6자리 인증 코드 생성
        2) 인증 코드 및 생성 시각을 메모리에 저장
        3) SMS 메시지 작성 및 전송
        4) 전송 성공 시 확인 메시지를 반환
        5) 전송 실패 시 `ServerException` 발생 */

/* 3. verifyCode(Map<String, Object> request):
      - 사용자로부터 입력받은 인증 코드를 검증하는 메서드
      - 주요 기능:
        1) 저장된 인증 코드와 입력된 인증 코드 비교
        2) 인증 코드의 유효 시간 확인
            - 만료된 경우 `VerificationException` 발생
        3) 인증 성공 시 메모리에서 인증 코드 삭제
        4) 성공 메시지를 반환 */

@Service
public class SmsService {

    // 인증 코드를 저장할 HashMap (메모리 내에서 관리)
    private final Map<String, Map<String, Object>> verificationCodes = new HashMap<>();
    private final DefaultMessageService messageService;
    private final SmsProperties smsProperties;

    public SmsService(SmsProperties smsProperties) {
        // SmsProperties를 사용해 DefaultMessageService 초기화
        this.smsProperties = smsProperties;
        this.messageService = NurigoApp.INSTANCE.initialize(
            smsProperties.getApiKey(),
            smsProperties.getApiSecretKey(),
            smsProperties.getProvider()
        );
    }

    // 인증코드 전송
    public Map<String, Object> sendVerificationMessage(Map<String, Object> request) {

        // 인증 코드 생성

        String toTelno = (String) request.get("telno");
    
        LocalDateTime sentAt = LocalDateTime.now();
    
        Random random = new Random();
        String code = String.format("%06d", random.nextInt(1000000));
    
        // HashMap에 전화번호를 키로 사용하여 인증 코드, 생성 시각 저장
        Map<String, Object> verificationData = new HashMap<>();
        verificationData.put("code", code);
        verificationData.put("sentAt", sentAt);
        verificationCodes.put(toTelno, verificationData);
    
        // 메시지 생성 및 전송
        Message message = new Message();
        message.setFrom(smsProperties.getSender());
    
        message.setTo(toTelno);
    
        String text = "입찰시스템 사용자 등록용 인증 코드: " + code + " (유효 시간: " + smsProperties.getExpirationTimeInMinutes() + "분)";
        message.setText(text);
    
        // CoolSMS 메시지 전송
        try {
            // SingleMessageSentResponse smsresponse = messageService.sendOne(new SingleMessageSendingRequest(message));    //테스트 단계에서는 실제로 전화번화로 전송하지 않음.
    
            Map<String, Object> response = new HashMap<>();
            response.put("message", "인증 코드가 성공적으로 전송되었습니다. "+ code);
            return response;
        } catch (Exception e) {
            throw new ServerException("시스템 오류 : 인증코드 전송 중 오류가 발생하였습니다. " + code, e);
        }
    }
    
    //인증 코드 검증 
    public Map<String, Object> verifyCode(Map<String, Object> request) {
        LocalDateTime verifiedAt = LocalDateTime.now();
        String code = (String) request.get("authNumber");
        String toTelno = (String) request.get("telno");
        Map<String, Object> verificationData = verificationCodes.get(toTelno);

        if (verificationData == null) {
            throw new VerificationException("시스템 오류 : 저장된 인증정보가 없습니다.");
        }

        String storedCode = (String) verificationData.get("code");
        LocalDateTime sentAt = (LocalDateTime) verificationData.get("sentAt");

        // 인증 코드가 일치하는지 확인
        if (!storedCode.equals(code)) {
            throw new VerificationException("인증코드가 일치하지 않습니다.");
        }

        // 인증 코드가 만료되었는지 확인
        if (verifiedAt.isAfter(sentAt.plusMinutes(smsProperties.getExpirationTimeInMinutes())) ){
            throw new VerificationException("인증유효시간이 지났습니다. 인증코드 재발송을 눌러주세요.");
        }

        // 인증 코드 사용 후 삭제
        verificationCodes.remove(toTelno);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "인증 코드가 일치합니다.");
        return response;
    }
}