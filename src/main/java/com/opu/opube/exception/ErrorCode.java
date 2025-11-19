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

    MAIL_SEND_FAILED("01007", "이메일 전송에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    DUPLICATE_EMAIL("01008", "이미 가입된 이메일입니다.", HttpStatus.CONFLICT),
    MEMBER_NOT_FOUND("01009", "회원 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_PASSWORD("01010", "비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    EMAIL_NOT_VERIFIED("01011", "이메일 인증이 완료되지 않았습니다.", HttpStatus.FORBIDDEN),
    REFRESH_TOKEN_NOT_FOUND("01012", "저장된 리프레시 토큰이 없습니다.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_MISMATCH("01013", "리프레시 토큰이 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN("01014", "유효하지 않은 리프레시 토큰입니다.", HttpStatus.UNAUTHORIZED),
    LOGOUT_USER("01015", "로그아웃된 사용자입니다.", HttpStatus.UNAUTHORIZED),

    OAUTH_LOGIN_FAILED("01016", "소셜 로그인에 실패했습니다.", HttpStatus.UNAUTHORIZED),
    DUPLICATE_PROVIDER_MEMBER("01017", "이미 가입된 소셜 계정입니다.", HttpStatus.CONFLICT),
    INVALID_NICKNAME_LENGTH("01018", "닉네임 길이가 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_BIO_LENGTH("01019", "Bio 길이가 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD_RESET_TOKEN("01020", "유효하지 않은 비밀번호 재설정 토큰입니다.", HttpStatus.UNAUTHORIZED),
    EMAIL_ALREADY_VERIFIED("01021", "이미 이메일 인증이 완료된 계정입니다.", HttpStatus.BAD_REQUEST),
    NOTIFICATION_TYPE_NOT_FOUND("02001", "알림 타입을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    NOTIFICATION_NOT_FOUND("02002", "알림을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);



    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
