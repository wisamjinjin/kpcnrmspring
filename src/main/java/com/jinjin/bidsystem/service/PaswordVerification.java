package com.jinjin.bidsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.jinjin.bidsystem.mapper.UserMapper;
import com.jinjin.bidsystem.service.ExceptionService.*;

import java.util.Map;

/* 주요 메서드 설명 */
/* 1. verifyPassword(Map<String, Object> request):
      - 입력된 사용자 ID와 비밀번호를 기반으로 사용자 자격을 확인
      - 프로세스:
        1) `UserMapper`를 사용하여 사용자 정보를 DB에서 조회
        2) 조회된 비밀번호(암호화된 값)와 입력된 비밀번호(평문)를 비교
        3) 비밀번호가 일치하면 사용자 정보를 반환
        4) 비밀번호 불일치 시 `PasswordMismatchException` 발생
        5) 사용자를 찾지 못하면 `NotFoundException` 발생 */

@Service
public class PaswordVerification {

    @Autowired
    private UserMapper userMapper;

   
    // 사용자 테이블에서 id와 password로 자격 확인 
    public Map<String, Object> verifyPassword(Map<String, Object> request) throws Exception {
        Map<String, Object> results;
        try {
            // 사용자 정보 가져오기
            results = userMapper.getUserByQuery(request);
            if (results == null || results.isEmpty()) {
                throw new NotFoundException(null);
            }

            String inputpassword = (String) request.get("password");   // 입력된 비밀번호
            String encodedPassword = (String) results.get("password"); // 테이블에 저장된 암호화된 비밀번호

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (passwordEncoder.matches(inputpassword, encodedPassword)) {
                return results;  
            } else {
                throw new PasswordMismatchException(null);  
            }
        } catch (BadRequestException | NotFoundException | PasswordMismatchException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }

}
