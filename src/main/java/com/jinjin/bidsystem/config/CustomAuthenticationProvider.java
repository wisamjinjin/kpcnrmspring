/**
 * 사용자 인증을 처리하는 CustomAuthenticationProvider, 예외 상황을 구분하도록 하기 위해 작성함. 예외는 Loginfiler에서 catch되어 반환됨,
 * 정의하지 않는 경우 userid notfound, password 오류가 BadCredentialsException로 반환되어 사용자에게 적절한 메시지를 반환하기 어렵다.
 */
package com.jinjin.bidsystem.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.jinjin.bidsystem.service.CustomUserDetailsService;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public CustomAuthenticationProvider(CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        try {
            // 사용자 정보 로드
            var userDetails = userDetailsService.loadUserByUsername(username);

            if (userDetails == null) {
                throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
            }

            // 비밀번호 검증
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new BadCredentialsException("비밀번호가 틀렸습니다.");
            }

            // 인증 성공
            return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

        } catch (UsernameNotFoundException ex) {
            throw ex;
        } catch (BadCredentialsException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AuthenticationException("인증 처리 중 예외가 발생했습니다.") {
                @Override
                public String getMessage() {
                    return ex.getMessage();
                }
            };
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
