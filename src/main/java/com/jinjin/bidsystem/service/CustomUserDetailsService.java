package com.jinjin.bidsystem.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jinjin.bidsystem.dto.CustomUserDetails;
import com.jinjin.bidsystem.entity.UserEntity;
import com.jinjin.bidsystem.repository.UserRepository;

// 주요 메서드 설명
// 1. loadUserByUsername(String username):
//    - Spring Security의 UserDetailsService를 구현한 메서드
//    - 사용자 로그인 시 전화번호(username)를 기준으로 사용자 정보를 로드
//    - `UserEntity`에서 조회된 데이터를 `CustomUserDetails` 객체로 변환하여 반환
//    - 데이터가 없을 경우 예외(`UsernameNotFoundException`) 발생

// 2. UserRepository 사용:
//    - `findByTelno(String telno)` 메서드를 호출하여 전화번호 기반 사용자 정보를 조회
//    - 필요에 따라 `findByUsername(String username)`으로 변경 가능 (주석 참고)

// 3. CustomUserDetails 객체 반환:
//    - 조회된 사용자 정보를 담은 `UserEntity` 객체를 래핑하여 Spring Security 인증에서 사용

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userData = userRepository.findByTelno(username);             //전화번호로 사용자 로그인
        // UserEntity userData = userRepository.findByUsername(username);       //사용자 이름으로 로그인
        // 사용자 데이터 출력
        if (userData == null) {
            throw new UsernameNotFoundException("UserEntity cannot be null");
        } else {
            return new CustomUserDetails(userData);
        }
    }
}
