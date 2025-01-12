/*
CustomUserDetails 클래스: Spring Security의 UserDetails 인터페이스를 구현한 사용자 정의 클래스

기능 및 용도:
1. Spring Security에서 인증 및 권한 부여에 사용할 사용자 정보를 제공.
2. UserEntity 객체를 기반으로 사용자 정보와 권한을 캡슐화.
3. 사용자 정의 메서드를 통해 추가 정보를 제공 (예: 전화번호 및 이메일).

구성 요소:
- userEntity:
  - UserEntity 객체를 참조하며 사용자의 기본 정보를 포함.

- getAuthorities():
  - 사용자의 권한을 반환.
  - UserEntity 객체에서 role을 가져와 GrantedAuthority 객체로 변환하여 반환.

- getPassword():
  - 사용자 비밀번호 반환.

- getUsername():
  - 사용자 이름 반환.

- isAccountNonExpired(), isAccountNonLocked(), isCredentialsNonExpired(), isEnabled():
  - 계정의 상태를 나타내며, 기본적으로 모두 true를 반환하여 활성화 상태로 설정.

사용자 정의 메서드:
- getTelno():
  - UserEntity 객체에서 사용자 전화번호를 반환.

- getEmail():
  - UserEntity 객체에서 사용자 이메일을 반환.

이 클래스는 Spring Security와 통합하여 인증 및 권한 관리를 간소화하고, 필요에 따라 사용자 데이터를 확장하여 추가적인 정보를 제공할 수 있음.
*/


package com.jinjin.bidsystem.dto;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.jinjin.bidsystem.entity.UserEntity;

public class CustomUserDetails implements UserDetails {

    private  final UserEntity userEntity;
    
    public  CustomUserDetails(UserEntity userEntity) {
        this.userEntity = userEntity;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collectionsOfAutority = new ArrayList<>();

        collectionsOfAutority.add(new GrantedAuthority() {
            
            @Override
            public String getAuthority(){
                return userEntity.getRole();
            }

        });
        return collectionsOfAutority;

    }

    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return userEntity.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // 사용자 정의 메서드
    public String getTelno() {
        return userEntity.getTelno(); // 전화번호 반환
    }

    public String getEmail() {
        return userEntity.getEmail(); // 이메일 반환
    }
    
}
