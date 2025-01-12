package com.jinjin.bidsystem.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.jinjin.bidsystem.dto.UserRegistrationDto;
import com.jinjin.bidsystem.entity.UserEntity;
import com.jinjin.bidsystem.repository.UserRepository;
import com.jinjin.bidsystem.service.ExceptionService.*;

/* 주요 메서드 설명 */
/* 1. RegisterService 생성자:
      - `UserRepository`와 `BCryptPasswordEncoder`를 초기화
      - 사용자 데이터 저장 및 비밀번호 암호화를 위한 의존성 주입 */
      
/* 2. registerProcess(UserRegistrationDto registerDTO):
      - 사용자 등록 프로세스를 처리하는 메서드
      - 주요 기능:
        1) DTO에서 사용자 입력 데이터(username, password, email, telno, role) 추출
        2) 전화번호 중복 여부 확인
            - 중복된 경우 `DuplicateKeyException` 발생
        3) `UserEntity` 객체를 생성하고 값 설정
            - 비밀번호는 `BCryptPasswordEncoder`로 암호화하여 저장
        4) 사용자 데이터를 `UserRepository`를 통해 저장
        5) 저장 성공 여부를 확인하고 응답 메시지 반환
        6) 저장 실패 시 `ZeroAffectedRowException` 발생 */


@Service
public class RegisterService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    public RegisterService(UserRepository userRepository,BCryptPasswordEncoder bCryptPasswordEncoder ) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public Map<String, Object> registerProcess(UserRegistrationDto registerDTO) {

        // DTO에서 값 추출
    
        String username = registerDTO.getUsername();
    
        String password = registerDTO.getPassword();
    
        String email = registerDTO.getEmail();
    
        String telno = registerDTO.getTelno();
    
        String role = registerDTO.getRole();
    
        // 응답 객체 초기화
        Map<String, Object> response = new HashMap<>();
    
        // 중복 사용자 확인
        Boolean isExist = userRepository.existsByTelno(telno);
        if (isExist) {
            throw new CustomDuplicateKeyException("중복된 사용자가 있습니다.");
        }
    
        // UserEntity 객체 생성 및 값 설정
        UserEntity data = new UserEntity();
        data.setUsername(username);
    
        data.setPassword(bCryptPasswordEncoder.encode(password));
    
        data.setEmail(email);
    
        data.setTelno(telno);
    
        data.setRole(role);
    
        // 저장 및 ID 확인
        Long newId = userRepository.save(data).getId();
    
        // 결과 처리
        if (newId > 0) {
            response.put("message", "성공적으로 등록되었습니다.");
            return response;
        } else {
            throw new ZeroAffectedRowException(null);
        }
    }
    
}