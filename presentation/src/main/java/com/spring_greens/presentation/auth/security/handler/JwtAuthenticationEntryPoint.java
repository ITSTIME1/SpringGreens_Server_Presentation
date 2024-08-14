package com.spring_greens.presentation.auth.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring_greens.presentation.auth.config.JwtProperties;
import com.spring_greens.presentation.auth.dto.CustomUser;
import com.spring_greens.presentation.auth.dto.response.LoginResponse;
import com.spring_greens.presentation.auth.entity.RefreshToken;
import com.spring_greens.presentation.auth.security.provider.JwtProvider;
import com.spring_greens.presentation.auth.util.CookieUtil;
import com.spring_greens.presentation.global.api.ApiResponse;
import com.spring_greens.presentation.global.enums.JwtErrorCode;
import com.spring_greens.presentation.auth.exception.JwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * Handles authentication errors related to JWT tokens.
 * <p>
 * Implements {@link AuthenticationEntryPoint} to manage authentication failures and errors
 * specifically related to JWT (JSON Web Tokens).
 * </p>
 * <p>
 * including:
 * <ul>
 *     <li>Processes and logs authentication error based on the JWT error code.</li>
 *     <li>Validates and updates expired access tokens using the refresh token, updating both the cookie and database if valid.</li>
 *     <li>Sends JSON responses with appropriate error messages or new tokens, depending on the error type.</li>
 * </ul>
 * </p>
 *
 * @author 01223lsh
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;
    private final ObjectMapper objectMapper;

    /**
     * Handles authentication errors and sends appropriate responses.
     * <p>
     * Checks the {@link JwtErrorCode} attribute from the request to determine the specific error and
     * processes it accordingly. Logs the error and sends a JSON response with the error details.
     * </p>
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        JwtErrorCode jwtErrorCode = (JwtErrorCode) request.getAttribute("jwtErrorCode");
        if (jwtErrorCode == null) {
            /* authenticated() 로 인한 AuthenticationException이 발생하는 나머지 경우의 예외처리 추가 예정 */
            jwtErrorCode = JwtErrorCode.UNKNOWN_ERROR;
        }

        switch (jwtErrorCode) {
            case EXPIRED_TOKEN:
                handleExpiredAccessToken(request, response);
                break;
            default:
                sendErrorResponse(response, jwtErrorCode);
                break;
        }
    }

    /**
     * Adds the refresh token to the response cookie.
     * <p>
     * Deletes any existing refresh token cookie and adds a new one with the given token and expiration time.
     * </p>
     */
    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        CookieUtil.deleteCookie(request, response, JwtProvider.REFRESH_TOKEN_NAME);
        CookieUtil.addCookie(response, JwtProvider.REFRESH_TOKEN_NAME, refreshToken, jwtProperties.getRefreshTokenExpiration());
    }

    /**
     * Handles expired tokens by validating the refresh token and reissuing new tokens if valid.
     * <p>
     * Retrieves the refresh token from the cookie, validates it, and checks if it matches
     * the stored token in the database. If valid, generates new tokens and responds with the new access token.
     * </p>
     */
    private void handleExpiredAccessToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestRefreshToken = CookieUtil.getCookieValue(request, JwtProvider.REFRESH_TOKEN_NAME);
        if (!validRefreshToken(requestRefreshToken)) {
            sendErrorResponse(response, JwtErrorCode.MALFORMED_REFRESH_TOKEN);
            return;
        }

        CustomUser customUser = jwtProvider.getCustomUser(requestRefreshToken);
        RefreshToken storedRefreshToken = jwtProvider.getRefreshTokenFromDB(customUser.getId());
        if (storedRefreshToken == null) {
            sendErrorResponse(response, JwtErrorCode.MALFORMED_REFRESH_TOKEN);
            return;
        }
        if (!storedRefreshToken.getRefreshToken().equals(requestRefreshToken)) {
            sendErrorResponse(response, JwtErrorCode.USED_REFRESH_TOKEN);
            return;
        }

        refreshTokensAndSendResponse(request, response, customUser);
        log.info("Successfully refreshed access token for user ID: {}", customUser.getId());
    }

    /**
     * Refreshes access and refresh tokens and sends the response with the new access token.
     * <p>
     * Generates new access and refresh tokens, updates the refresh token in the cookie,
     * and sends a JSON response with the new access token.
     * </p>
     */
    private void refreshTokensAndSendResponse(HttpServletRequest request, HttpServletResponse response, CustomUser customUser) throws IOException {
        String accessToken = jwtProvider.generateAccessToken(customUser);
        String refreshToken = jwtProvider.generateRefreshToken(customUser);

        addRefreshTokenToCookie(request, response, refreshToken);

        LoginResponse loginResponse = LoginResponse.builder()
                .access_token(accessToken)
                .build();

        ApiResponse<LoginResponse> apiResponse = ApiResponse.fail(JwtErrorCode.TOKEN_REISSUED, loginResponse);
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }

    private void sendErrorResponse(HttpServletResponse response, JwtErrorCode jwtErrorCode) throws IOException {
        ApiResponse<?> apiResponse = ApiResponse.fail(jwtErrorCode, null);
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }

    private boolean validRefreshToken(String refreshToken) {
        try {
            return jwtProvider.validToken(refreshToken);
        } catch (JwtException.JwtNotValidateException e) {
            return false;
        }
    }
}
