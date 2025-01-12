// 전역 예외 처리를 담당하는 클래스
// 이 클래스는 service에서 발생한 예외(ExceptionService.java)를 처리하고,
// 적절한 HTTP 상태 코드 및 에러 메시지를 클라이언트에게 반환
/* ErrorResponse는 아래와 같은 형태로 반환됨
    {
        "statusCode": 404,
        "status": "Not Found",
        "message": "요청한 리소스를 찾을 수 없습니다."
    }
*/

package com.jinjin.bidsystem.controller;

import java.io.UnsupportedEncodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.jinjin.bidsystem.dto.ErrorResponse;
import com.jinjin.bidsystem.service.ExceptionService.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ServerException을 처리
    // 서버에서 발생한 내부 에러를 나타내며 HTTP 상태 코드는 500
    @ExceptionHandler(ServerException.class)
    public ResponseEntity<ErrorResponse> handleServerException(ServerException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // DataAccessException을 처리
    // 데이터 접근 중 발생한 예외이며, HTTP 상태 코드는 500
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // PasswordMismatchException을 처리
    // 비밀번호 불일치로 인해 발생하며, HTTP 상태 코드는 400
    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ErrorResponse> handlePasswordMismatchException(PasswordMismatchException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // DuplicateKeyException을 처리
    // 데이터베이스에서 키 중복 오류가 발생했을 때 처리하며, HTTP 상태 코드는 409
    @ExceptionHandler(CustomDuplicateKeyException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateKeyException(CustomDuplicateKeyException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    // NoDataException을 처리
    // 요청된 데이터가 없을 경우 발생하며, HTTP 상태 코드는 404
    @ExceptionHandler(NoDataException.class)
    public ResponseEntity<ErrorResponse> handleNoDataException(NoDataException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // ZeroAffectedRowException을 처리
    // 데이터베이스 작업에서 영향받은 행이 없는 경우 발생하며, HTTP 상태 코드는 409
    @ExceptionHandler(ZeroAffectedRowException.class)
    public ResponseEntity<ErrorResponse> handleZeroAffectedRowException(ZeroAffectedRowException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    // UnauthorizedException을 처리
    // 인증 실패로 발생하며, HTTP 상태 코드는 401
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    // VerificationException을 처리
    // 사용자 인증 또는 검증 실패 시 발생하며, HTTP 상태 코드는 401
    @ExceptionHandler(VerificationException.class)
    public ResponseEntity<ErrorResponse> handleVerificationException(VerificationException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    // NoSuchAlgorithmException을 처리
    // 암호화 알고리즘 관련 예외가 발생하며, HTTP 상태 코드는 501
    @ExceptionHandler(NoSuchAlgorithmException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchAlgorithmException(NoSuchAlgorithmException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_IMPLEMENTED);
    }

    // NurigoException을 처리
    // 외부 서비스와의 통신에서 발생하는 예외로 HTTP 상태 코드는 500
    @ExceptionHandler(NurigoException.class)
    public ResponseEntity<ErrorResponse> handleNurigoException(NurigoException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // PgException을 처리
    // 결제 처리 중 발생하는 예외이며, HTTP 상태 코드는 500
    @ExceptionHandler(PgException.class)
    public ResponseEntity<ErrorResponse> handlePgException(PgException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // UnsupportedEncodingException을 처리
    // 지원되지 않는 인코딩이 사용된 경우 발생하며, HTTP 상태 코드는 501
    @ExceptionHandler(UnsupportedEncodingException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedEncodingException(UnsupportedEncodingException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_IMPLEMENTED);
    }

    // NotFoundException을 처리
    // 요청된 리소스를 찾을 수 없는 경우 발생하며, HTTP 상태 코드는 404
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // BadRequestException을 처리
    // 클라이언트의 요청이 잘못된 경우 발생하며, HTTP 상태 코드는 400
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // ConflictException을 처리
    // 리소스 간 충돌이 발생했을 때 처리하며, HTTP 상태 코드는 409
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    // 공통적으로 ErrorResponse를 생성하여 반환하는 메서드
    // 이 메서드는 예외 메시지와 HTTP 상태 코드를 받아 적절한 응답 객체를 생성합니다.
    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status) {
        ErrorResponse errorResponse = new ErrorResponse(
            status.value(),        // HTTP 상태 코드
            status.getReasonPhrase(), // HTTP 상태 설명 (예: BAD_REQUEST)
            message != null ? message : "에러가 발생했습니다." // 에러 메시지, 기본값은 "에러가 발생했습니다."
        );
        return new ResponseEntity<>(errorResponse, status); // ResponseEntity에 상태 코드와 함께 반환
    }
}
