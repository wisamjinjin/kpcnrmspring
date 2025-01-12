package com.jinjin.bidsystem.mapper;
import org.apache.ibatis.annotations.Mapper;
import java.util.Map;

@Mapper
public interface UserMapper {

    // 사용자 정보 조회 (ID, 전화번호, 이메일 등)
    Map<String, Object> getUserByQuery(Map<String, Object> request);

    // 이메일 건수 확인(중복확인 용)
    Map<String, Object> getEmailCount(Map<String, Object> request);

    // 전화번호 건수 확인(중복확인 용)
    Map<String, Object> getTelnoCount(Map<String, Object> request);
    
    // 사용자 등록
    int registerUser(Map<String, Object> request);

    // 사용자 정보 수정
    int updateUser(Map<String, Object> request);

    // 비밀번호 변경
    int changePassword(Map<String, Object> request);
}
