package com.spring_greens.presentation.global.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum JwtErrorCode {
    UNKNOWN_TOKEN("Token does not exist.", HttpStatus.BAD_REQUEST),
    WRONG_SIGNATURE_TOKEN("Invalid JWT signature.", HttpStatus.BAD_REQUEST),
    MALFORMED_TOKEN("Invalid JWT token.", HttpStatus.BAD_REQUEST),
    UNSUPPORTED_TOKEN("Unsupported token.", HttpStatus.BAD_REQUEST),
    INVALID_CLAIMS_TOKEN("Invalid claims in JWT token.", HttpStatus.BAD_REQUEST),
    EXPIRED_TOKEN("Token has expired.", HttpStatus.BAD_REQUEST),

    TOKEN_REISSUED("Expired access token. A new refresh token has been issued.", HttpStatus.UNAUTHORIZED),
    MALFORMED_REFRESH_TOKEN("Expired access token. Refresh token is invalid.", HttpStatus.BAD_REQUEST),
    USED_REFRESH_TOKEN("Expired access token. Refresh token has already been used.", HttpStatus.BAD_REQUEST),

    LOGOUT_FAIL_MALFORMED_TOKEN("Refresh token is malformed or invalid.",HttpStatus.BAD_REQUEST),
    LOGOUT_FAIL_USED_TOKEN("Refresh token has already been used.",HttpStatus.BAD_REQUEST),
    LOGOUT_FAIL_DB_ERROR("Failed to remove refresh token from the database", HttpStatus.INTERNAL_SERVER_ERROR),

    ACCESS_DENIED_TOKEN("Access denied.", HttpStatus.FORBIDDEN),
    UNKNOWN_ERROR("Unknown error. Please contact the administrator.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus httpStatus;

    JwtErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
