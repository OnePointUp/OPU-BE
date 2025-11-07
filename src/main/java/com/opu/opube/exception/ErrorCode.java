package com.opu.opube.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum ErrorCode {

    VALIDATION_ERROR("01000", "입력 값 검증 오류입니다.", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("01001", "내부 서버 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED_USER("01002", "인증되지 않은 사용자입니다.",HttpStatus.UNAUTHORIZED),
    EXPIRED_JWT("01003", "JWT 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_JWT("01004", "잘못된 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED),
    UNSUPPORTED_JWT("01005", "지원하지 않는 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED),
    EMPTY_JWT("01006", "JWT 클레임이 비어있습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_FILE_TYPE("05003","유효하지 않은 파일 타입입니다." ,HttpStatus.BAD_REQUEST),

    ;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
