package com.jinjin.bidsystem.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 예외 처리 및 로깅을 위한 ExceptionService 클래스
 * 이 클래스는 다양한 커스텀 예외를 정의하며, 각 예외 발생 시 적절한 로깅깅
 */
@Service
public class ExceptionService {
    // 로깅을 위한 Logger 설정
    private static final Logger logger = LoggerFactory.getLogger(BidService.class);

    // ======================= 시스템 오류 예외 처리 ===============================

    /**
     * 서버에서 발생하는 일반적인 오류를 처리하는 예외 클래스
     */
    public static class ServerException extends RuntimeException {
        public ServerException(String message, Throwable cause) {
            super(message != null && !message.isEmpty() ? message : "오류: 서버에서 오류가 발생하였습니다.");
            logger.error("\n\n+++++++ ServerException: A system error occurred. " + message + " --- Cause : " + cause.getMessage(), cause);
        }
    }

    /**
     * 데이터베이스 접근 중 발생하는 오류를 처리하는 예외 클래스
     */
    public static class DataAccessException extends RuntimeException {
        public DataAccessException(String message, Throwable cause) {
            super(message != null && !message.isEmpty() ? message : "오류: 데이터베이스 접근 중 오류가 발생하였습니다.");
            logger.error("\n\n+++++++ DataAccessException: An error occurred while accessing the database. " + message + " --- Cause : " + cause.getMessage(), cause);
        }
    }

    /**
     * 서버 상태가 잘못된 경우 발생하는 오류를 처리하는 예외 클래스
     */
    public static class IllegalStateException extends RuntimeException {
        public IllegalStateException(String message, Throwable cause) {
            super(message != null && !message.isEmpty() ? message : "오류: 서버에서 오류가 발생하였습니다(Illegal State).");
            logger.error("\n\n+++++++ IllegalStateException: A system error occurred. " + message + " --- Cause : " + cause.getMessage(), cause);
        }
    }

    /**
     * 잘못된 인자가 전달될 경우 발생하는 오류를 처리하는 예외 클래스
     */
    public static class IllegalArgumentException extends RuntimeException {
        public IllegalArgumentException(String message, Throwable cause) {
            super(message != null && !message.isEmpty() ? message : "오류: 서버에서 오류가 발생하였습니다(Illegal Argument).");
            logger.error("\n\n+++++++ IllegalArgumentException: A system error occurred. " + message + " --- Cause : " + cause.getMessage(), cause);
        }
    }

    /**
     * 사용되지 않는 알고리즘 관련 오류를 처리하는 예외 클래스
     */
    public static class NoSuchAlgorithmException extends RuntimeException {
        public NoSuchAlgorithmException(String message, Throwable cause) {
            super(message != null && !message.isEmpty() ? message : "오류: 서버에서 알고리즘을 찾을 수 없습니다.");
            logger.error("\n\n\n\n+++++++ NoSuchAlgorithmException: No such algorithm found. " + message + " --- Cause : " + cause.getMessage(), cause);
        }
    }

    /**
     * 인코딩 과정 중 발생하는 오류를 처리하는 예외 클래스
     */
    public static class UnsupportedEncodingException extends RuntimeException {
        public UnsupportedEncodingException(String message, Throwable cause) {
            super(message != null && !message.isEmpty() ? message : "오류: 서버에서 인코딩 중 오류가 발생하였습니다.");
            logger.error("\n\n+++++++ UnsupportedEncodingException: An error occurred during encoding. " + message + " --- Cause : " + cause.getMessage(), cause);
        }
    }

    /**
     * 인증 코드 전송 과정에서 발생하는 오류를 처리하는 예외 클래스
     */
    public static class NurigoException extends RuntimeException {
        public NurigoException(String message, Throwable cause) {
            super(message != null && !message.isEmpty() ? message : "오류: 인증코드 전송 중 오류가 발생하였습니다.");
            logger.error("\n\n+++++++ NurigoException: An error occurred during verification code sending. " + message + " --- Cause : " + cause.getMessage(), cause);
        }
    }

    /**
     * 결제 처리 중 발생하는 오류를 처리하는 예외 클래스
     */
    public static class PgException extends RuntimeException {
        public PgException(String message, Throwable cause) {
            super(message != null && !message.isEmpty() ? message : "오류: 결제 요청 중 오류가 발생하였습니다.");
            logger.error("\n\n+++++++ PgException: An error occurred during payment processing. " + message + " --- Cause : " + cause.getMessage(), cause);
        }
    }

    /**
     * 처리된 데이터 행이 없는 경우 발생하는 오류를 처리하는 예외 클래스
     */
    public static class ZeroAffectedRowException extends RuntimeException {
        public ZeroAffectedRowException(String message) {
            super(message != null && !message.isEmpty() ? message : "오류: 처리된 행이 없습니다.");
            logger.error("\n\n+++++++ ZeroAffectedRowException: The operation failed to affect any rows.", message);
        }
    }

    // ======================= 사용자 오류 예외 처리 ===============================

    /**
     * 데이터 중복으로 인해 발생하는 오류를 처리하는 예외 클래스
     */
    public static class CustomDuplicateKeyException extends RuntimeException {
        public CustomDuplicateKeyException(String message) {
            super(message != null && !message.isEmpty() ? message : "중복된 정보입니다. 입력내용을 확인하세요.");
            logger.info("\n\n+++++++ DuplicateKeyException: Conflicting information exists. Message: " + (message != null ? message : ""));
        }
    }

    /**
     * 권한이 없는 경우 발생하는 오류를 처리하는 예외 클래스
     */
    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message != null && !message.isEmpty() ? message : "권한이 없습니다.");
            logger.info("\n\n+++++++ UnauthorizedException: No permission. Message: " + (message != null ? message : ""));
        }
    }

    /**
     * 인증 실패로 인해 발생하는 오류를 처리하는 예외 클래스
     */
    public static class VerificationException extends RuntimeException {
        public VerificationException(String message) {
            super(message != null && !message.isEmpty() ? message : "인증이 실패하였습니다. 다시 인증해주세요.");
            logger.info("\n\n+++++++ VerificationException: Verification failed. Message: " + (message != null ? message : ""));
        }
    }

    /**
     * 요청된 데이터를 찾을 수 없는 경우 발생하는 오류를 처리하는 예외 클래스
     */
    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message != null && !message.isEmpty() ? message : "해당되는 정보를 찾을 수 없습니다. 입력내용을 확인하세요.");
            logger.info("\n\n+++++++ NotFoundException: No matching information found. Message: " + (message != null ? message : ""));
        }
    }

    /**
     * 데이터가 없는 경우 발생하는 오류를 처리하는 예외 클래스
     */
    public static class NoDataException extends RuntimeException {
        public NoDataException(String message) {
            super(message != null && !message.isEmpty() ? message : "해당되는 정보가 없습니다.");
            logger.info("\n\n+++++++ NoDataException: No information available. Message: " + (message != null ? message : ""));
        }
    }

    /**
     * 잘못된 요청 파라미터로 인해 발생하는 오류를 처리하는 예외 클래스
     */
    public static class BadRequestException extends RuntimeException {
        public BadRequestException(String message) {
            super(message != null && !message.isEmpty() ? message : "요청 파라메터의 형식에 오류가 있습니다.");
            logger.info("\n\n+++++++ BadRequestException: There is an error in the request parameters. Message: " + (message != null ? message : ""));
        }
    }

    /**
     * 중복된 데이터로 인해 발생하는 오류를 처리하는 예외 클래스
     */
    public static class ConflictException extends RuntimeException {
        public ConflictException(String message) {
            super(message != null && !message.isEmpty() ? message : "중복된 정보가 존재합니다.");
            logger.info("\n\n+++++++ ConflictException: Conflicting information exists. Message: " + (message != null ? message : ""));
        }
    }

    /**
     * 비밀번호 불일치로 인해 발생하는 오류를 처리하는 예외 클래스
     */
    public static class PasswordMismatchException extends RuntimeException {
        public PasswordMismatchException(String message) {
            super(message != null && !message.isEmpty() ? message : "비밀번호가 일치하지 않습니다.");
            logger.info("\n\n+++++++ PasswordMismatchException: Password does not match. Message: " + (message != null ? message : ""));
        }
    }
}
