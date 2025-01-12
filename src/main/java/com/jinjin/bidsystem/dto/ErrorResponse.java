package com.jinjin.bidsystem.dto;

// 클라이언트에게 반환되는 에러 응답 객체를 나타내는 클래스
public class ErrorResponse {
    private int status;       // HTTP 상태 코드 (예: 400, 404, 500)
    private String errorCode; // 에러 코드 (예: "Bad Request", "Not Found")
    private String message;   // 에러 메시지 (예: "요청이 잘못되었습니다.")
    private long timestamp;   // 에러 발생 시간(타임스탬프)

    // 생성자: ErrorResponse 객체를 초기화
    public ErrorResponse(int status, String errorCode, String message) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message; 
        this.timestamp = System.currentTimeMillis();
    }

    // HTTP 상태 코드 반환
    public int getStatus() {
        return status;
    }

    // HTTP 상태 코드 설정
    public void setStatus(int status) {
        this.status = status;
    }

    // 에러 코드 반환
    public String getErrorCode() {
        return errorCode;
    }

    // 에러 코드 설정
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    // 에러 메시지 반환
    public String getMessage() {
        return message;
    }

    // 에러 메시지 설정
    public void setMessage(String message) {
        this.message = message;
    }

    // 타임스탬프 반환
    public long getTimestamp() {
        return timestamp;
    }

    // 타임스탬프 설정
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
