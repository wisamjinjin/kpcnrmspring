package com.jinjin.bidsystem.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import com.jinjin.bidsystem.service.ExceptionService.*;

import java.util.HashMap;
import java.util.Map;

//jwt방식으로 변경되어 사용하지 않음
@Service
public class SessionService {

    // 세션 변수 설정 함수
    public void setSessionAttributes(HttpSession session, String userContext, String userId, String telno, String role, String userName) {
        session.setAttribute("userContext", userContext);
        session.setAttribute("userId", userId);
        session.setAttribute("telno", telno);
        session.setAttribute("userName", userName);
        session.setAttribute("role", role); 
    }

    // 쿠키 설정 함수
    public void setLoginCookie(HttpSession session, HttpServletResponse httpResponse) {
        // HttpSession에서 세션 ID를 가져옴 (Java 표준 세션 ID)
        String sessionId = session.getId();         
        // JSESSIONID 쿠키 생성
        Cookie cookie = new Cookie("JSESSIONID", sessionId);
        cookie.setHttpOnly(true);  // JavaScript에서 접근 불가하도록 설정
        cookie.setSecure(true);    // HTTPS를 사용하는 경우 true로 설정
        cookie.setPath("/");       // 모든 경로에서 쿠키 유효
        cookie.setMaxAge(60 * 60); // 1시간 동안 유효 (60초  * n)
        httpResponse.addHeader("Set-Cookie", cookie.getName() + "=" + cookie.getValue() + "; Path=" + cookie.getPath() + "; HttpOnly; Secure; SameSite=None");
        httpResponse.addCookie(cookie);
    }
    
    // 쿠키 삭제 함수
    public void clearLoginCookie(HttpServletResponse response) {
        // JSESSIONID 쿠키를 삭제하기 위한 설정
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setMaxAge(0);   // 쿠키 만료 시간 0으로 설정하여 즉시 삭제
        cookie.setPath("/");   // 모든 경로에서 쿠키가 삭제되도록 설정
        cookie.setHttpOnly(true);  // 보안 설정: JavaScript에서 접근 불가
        cookie.setSecure(true);    // HTTPS 환경에서만 전송되도록 설정 (HTTPS 사용 시 true)
        // cookie.setSameSite("Strict");  // SameSite 설정 (옵션)
        response.addCookie(cookie);  // 응답에 쿠키를 추가하여 클라이언트에 삭제 요청
    }

    // 세션에서 사용자 ID를 가져오는 공통 함수
    public Map<String, Object> getSessionUser(HttpSession session) {
    Map<String, Object> response = new HashMap<>();

    try {
        // 세션이 존재하는지 확인
        if (session == null || session.getAttribute("telno") == null) {
            // 세션이 만료되었거나 사용자 정보가 없는 경우
            throw new UnauthorizedException("세션이 유효하지 않습니다. 다시 로그인 해주세요.");
        }

        // 세션에서 사용자 정보를 가져옴
        String userContext = (String) session.getAttribute("userContext");
        String userId = (String) session.getAttribute("userId");
        String telno = (String) session.getAttribute("telno");
        String role = (String) session.getAttribute("role");
        String userName = (String) session.getAttribute("userName");

        if (telno != null) {
            // 사용자 정보가 존재하면 성공 응답을 반환
            response.put("status", "success");
            response.put("userContext", userContext);
            response.put("userId", userId);
            response.put("telno", telno);
            response.put("role", role);
            response.put("userName", userName);
        } else {
            // 사용자 정보가 없으면 오류 메시지 반환
            throw new UnauthorizedException("세션 사용자 정보가 없습니다.");
        }
    } catch (UnauthorizedException e) {
        throw e;
    } catch (Exception e) {
        throw new ServerException("세션 정보 접근 중 오류가 발생하였습니다.", e);
    }

    return response;
}


// 세션 clear 및 JSESSIONID 쿠키 삭제
public Map<String, Object> clearSession(HttpSession session, HttpServletResponse httpResponse) {
    try {
        // 세션 무효화
        session.invalidate();

        // JSESSIONID 쿠키 삭제
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setMaxAge(0);   // 즉시 만료
        cookie.setPath("/");   // 애플리케이션 전체 경로에 적용
        cookie.setHttpOnly(true);  // 보안 설정
        cookie.setSecure(true);    // HTTPS를 사용하는 경우에만 true
        httpResponse.addCookie(cookie);  // 클라이언트에 쿠키 삭제 요청

        // 로그아웃 성공 응답 데이터 생성
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "로그아웃 성공");

        return response;
    } catch (Exception e) {
        throw new ServerException("세션 종료에 오류가 발생하였습니다.", e);
    }
}

// 세션 복원 함수 
public Map<String, Object> restoreSession(Map<String, Object> request, HttpServletRequest httpRequest, HttpSession session, HttpServletResponse httpResponse) {
    String userContext = (String) request.get("userContext");
    String userId = (String) request.get("userId");
    String telno = (String) request.get("telno");
    String role = (String) request.get("role");
    String userName = (String) request.get("userName");

    if (session == null) {
        // 세션이 없으면 새로 생성
        session = httpRequest.getSession(true);
    }
    setSessionAttributes( session, userContext,
      userId,  telno,  role,  userName);
    setLoginCookie(session, httpResponse);

    // 로그아웃 성공 응답 데이터 생성
    Map<String, Object> response = new HashMap<>();
    response.put("status", "success");
    response.put("message", "세션복원 성공");
    return response;
}

}
