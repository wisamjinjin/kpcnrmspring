package com.jinjin.bidsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.jinjin.bidsystem.mapper.UserMapper;
import com.jinjin.bidsystem.service.ExceptionService.*;

import java.util.HashMap;
import java.util.Map;

/* 주요 메서드 설명 */
/* - getUserByQueryAndPassword(Map<String, Object> request):
       사용자 ID와 비밀번호를 기반으로 사용자 정보를 조회
       비밀번호를 제외한 사용자 정보를 반환 */

/* - getUserByQuery(Map<String, Object> request):
       사용자 정보를 특정 조건(query)에 따라 조회
       비밀번호를 제외한 정보를 반환 */

/* - getEmailCount(Map<String, Object> request):
       사용자 등록 또는 수정 시 이메일 중복 여부를 확인
       주요 기능:
         . DB에서 이메일 개수 확인 */

/* - getTelnoCount(Map<String, Object> request):
       사용자 등록 또는 수정 시 전화번호 중복 여부를 확인
       주요 기능:
         . DB에서 전화번호 개수 확인 */

/* - changePassword(Map<String, Object> request):
       사용자의 비밀번호를 변경
       주요 기능:
         . 입력받은 비밀번호를 암호화 및 저장
         . 업데이트 실패 시 `ZeroAffectedRowException` 발생 */

/* - updateUser(Map<String, Object> request):
       사용자 정보를 업데이트
       주요 기능:
         . 업데이트 실패 시 `ZeroAffectedRowException` 발생 */

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PaswordVerification paswordVerification;

    
    // 사용자 정보 조회
    public Map<String, Object> getUserByQueryAndPassword(Map<String, Object> request) {
        try {
            Map<String, Object> results = paswordVerification.verifyPassword(request);
            if (results == null || results.isEmpty()) {
                throw new NotFoundException(null);
            } else {
                // 패스워드를 제외한 새로운 Map 생성
                Map<String, Object> filteredResults = new HashMap<>();
                filteredResults.put("telno", results.get("telno"));
                filteredResults.put("username", results.get("username"));
                filteredResults.put("email", results.get("email"));
                filteredResults.put("role", results.get("role"));
                return filteredResults;
            }
        } catch (BadRequestException | NotFoundException | PasswordMismatchException e) {
            throw e;
        } catch (Exception e) {
            throw new ServerException(null,e);
        }
    }
    
    public Map<String, Object> getUserByQuery(Map<String, Object> request) {
        Map<String, Object> results;
        try {
            results = userMapper.getUserByQuery(request);
            if (results == null || results.isEmpty()) {
                throw new NotFoundException(null);
            } else {
                // 패스워드를 제외한 새로운 Map 생성
                Map<String, Object> filteredResults = new HashMap<>();
                filteredResults.put("telno", results.get("telno"));
                filteredResults.put("username", results.get("username"));
                filteredResults.put("email", results.get("email"));
                filteredResults.put("role", results.get("role"));
                return filteredResults;
            }
        } catch (BadRequestException | NotFoundException | PasswordMismatchException e) {
            throw e;
        } catch (Exception e) {
            throw new ServerException(null,e);
        }
    }

    // 사용자 등록 및 수정시 이메일 중복을 체크하기위해 호출 
    public Map<String, Object> getEmailCount(Map<String, Object> request) {
        // 요청에서 테이블 이름 가져오기

        Map<String, Object> results;
    
        try {
            // 사용자 또는 관리자 테이블에서 정보 가져오기
            results = userMapper.getEmailCount(request);
            return results;
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(null, e);
        }
    }    
    
    // 사용자 등록 및 수정시 전화 번호 중복을 체크하기위해 호출 
    public Map<String, Object> getTelnoCount(Map<String, Object> request) {
        // 요청에서 테이블 이름 가져오기
      try {  
        Map<String, Object> results;
            results = userMapper.getTelnoCount(request);
            return results;
        } catch (Exception e) {
            throw new DataAccessException(null, e);
        }
    }   

    // 비밀번호 변경
    public Map<String, Object> changePassword(Map<String, Object> request) {
        try {

            String inputPassword = (String) request.get("password");
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(inputPassword);
            request.put("newPassword", encodedPassword);
            int affectedRows =  userMapper.changePassword(request);
            if (affectedRows > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "성공적으로 수행되었습니다.");
                return response;
            } else {
                throw new ZeroAffectedRowException("오류 : 갱신된 행이 없습니다.");
            }
        } catch (NotFoundException e) {
            throw new NotFoundException(null);
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }
    
    // 사용자 정보 업데이트
    public Map<String, Object> updateUser(Map<String, Object> request) {
        try {
            int affectedRows =  userMapper.updateUser(request);
            if (affectedRows > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "성공적으로 수행되었습니다.");
                return response;
            } else {
                throw new ZeroAffectedRowException(null);
            }
        } catch (ZeroAffectedRowException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(null,e);
        }
    }
}
