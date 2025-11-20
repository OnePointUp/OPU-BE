package com.opu.opube.exception;

import com.opu.opube.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /* 비즈니스 예외처리 핸들러 */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();

        log.error("비즈니스 예외 발생: {}", e.getMessage(), e);

        ApiResponse<Void> response
                = ApiResponse.failure(errorCode.getCode(), errorCode.getMessage());
        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }

    /* 유효성 검사 예외처리 핸들러 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;

        log.error("유효성 검사 예외 발생: {}", e.getMessage(), e);

        StringBuilder errorMessage = new StringBuilder(errorCode.getMessage());
        for(FieldError error : e.getBindingResult().getFieldErrors()) {
            errorMessage.append(String.format("[%s : %s]", error.getField(), error.getDefaultMessage()));
        }
        ApiResponse<Void> response
                = ApiResponse.failure(errorCode.getCode(), errorMessage.toString());
        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }

    /* 예상치 못한 예외 처리 핸들러 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        log.error("예상치 못한 예외 발생: {}", e.getMessage(), e);

        ApiResponse<Void> response
                = ApiResponse.failure(errorCode.getCode(), errorCode.getMessage());
        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }

    /* Spring Security 인증 실패 예외 처리 핸들러 */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException e) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED_USER;

        log.error("인증 실패 예외 발생: {}", e.getMessage(), e);

        ApiResponse<Void> response
                = ApiResponse.failure(errorCode.getCode(), errorCode.getMessage());
        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }

    /* CustomJwtException 예외 처리 핸들러 */
    @ExceptionHandler(CustomJwtException.class)
    public ResponseEntity<ApiResponse<Void>> handleJwtException(CustomJwtException e) {

        log.error("custom jwt 예외 발생: {}", e.getMessage(), e);

        ApiResponse<Void> response
                = ApiResponse.failure(e.getErrorCode().getCode(), e.getErrorCode().getMessage());
        return new ResponseEntity<>(response, e.getErrorCode().getHttpStatus());
    }
}
