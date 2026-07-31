package com.poorbet.authservice.exception;

import org.springframework.http.HttpStatus;

public class InvalidRefreshTokenException extends BaseException {
    public InvalidRefreshTokenException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, "INVALID_REFRESH_TOKEN");
    }
}
