package com.opu.opube.feature.auth.command.domain.service;

import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class AuthDomainService {

    private static final int NICKNAME_MIN_LENGTH = 2;
    private static final int NICKNAME_MAX_LENGTH = 20;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,}$");

    public void validatePassword(String password) {
        if (password == null) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD, "비밀번호를 입력해주세요.");
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD_FORMAT);
        }
    }

    public void validateNickname(String nickname) {
        if (nickname == null ||
                nickname.length() < NICKNAME_MIN_LENGTH ||
                nickname.length() > NICKNAME_MAX_LENGTH) {
            throw new BusinessException(ErrorCode.INVALID_NICKNAME_LENGTH);
        }
    }
}

