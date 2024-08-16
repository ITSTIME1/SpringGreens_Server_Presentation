package com.spring_greens.presentation.auth.exception;

import com.spring_greens.presentation.global.enums.JwtErrorCode;
import lombok.Getter;

public abstract class JwtException extends RuntimeException {
    public JwtException(String message) {
        super(message);
    }
    public JwtException(String message, Throwable cause) {
        super(message, cause);
    }

    @Getter
    public static class JwtNotValidateException extends JwtException {
        private final JwtErrorCode jwtErrorCode;
        public JwtNotValidateException(JwtErrorCode jwtErrorCode) {
            super(jwtErrorCode.getMessage());
            this.jwtErrorCode = jwtErrorCode;
        }

        public JwtNotValidateException(JwtErrorCode jwtErrorCode,Throwable cause) {
            super(jwtErrorCode.getMessage(), cause);
            this.jwtErrorCode = jwtErrorCode;
        }
    }
}






