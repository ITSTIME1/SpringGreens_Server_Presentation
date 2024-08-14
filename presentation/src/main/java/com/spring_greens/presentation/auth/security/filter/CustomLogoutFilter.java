package com.spring_greens.presentation.auth.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring_greens.presentation.auth.dto.CustomUser;
import com.spring_greens.presentation.auth.entity.RefreshToken;
import com.spring_greens.presentation.auth.security.provider.JwtProvider;
import com.spring_greens.presentation.auth.util.CookieUtil;
import com.spring_greens.presentation.global.api.ApiResponse;
import com.spring_greens.presentation.global.enums.JwtErrorCode;
import com.spring_greens.presentation.auth.exception.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;
import java.io.IOException;

/**
 * Custom filter for handling logout requests and invalidating refresh tokens.
 * <p>
 * This filter intercepts requests to the "/logout" endpoint, processes the logout
 * by removing the refresh token from cookies, validating the token, and deleting
 * it from the database if valid. It also handles error responses and success responses.
 * </p>
 * <p>
 * including:
 * <ul>
 *     <li>Intercepts logout requests and handles</li>
 *     <li>Removes the refresh token cookie.</li>
 *     <li>Validates the refresh token.</li>
 *     <li>Retrieves user information and checks the token against the database.</li>
 *     <li>Deletes the token from the database if it matches.</li>
 *     <li>Sends appropriate success or error responses based on the outcome.</li>
 * </ul>
 * </p>
 *
 * @author 01223lsh
 */
@Slf4j
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {
    private static final String LOGOUT_PATH = "/api/logout";
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    /**
     * Processes requests and handles logout-specific logic.
     * <p>
     * Delegates to specific logout handling if the request URI is "/logout" and method is "POST".
     * </p>
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    /**
     * Handles the logout request by checking if the request URI matches "/logout" and the method is "POST".
     * <p>
     * If so, processes the logout. Otherwise, forwards the request to the next filter in the chain.
     * </p>
     */
    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (request.getRequestURI().equals(LOGOUT_PATH) && request.getMethod().equals("POST")) {
            handleLogout(request, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    /**
     * Handles the logout logic including removing the refresh token cookie, validating the token,
     * checking it against the database, and deleting it if valid.
     * <p>
     * Sends error or success responses based on the outcome.
     * </p>
     */
    private void handleLogout (HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestRefreshToken = CookieUtil.getCookieValue(request, JwtProvider.REFRESH_TOKEN_NAME);
        CookieUtil.deleteCookie(request, response, JwtProvider.REFRESH_TOKEN_NAME);

        if (!validRefreshToken(requestRefreshToken)) {
            sendErrorResponse(response, JwtErrorCode.LOGOUT_FAIL_MALFORMED_TOKEN);
            return;
        }

        CustomUser customUser = jwtProvider.getCustomUser(requestRefreshToken);
        RefreshToken storedRefreshToken = jwtProvider.getRefreshTokenFromDB(customUser.getId());

        if (storedRefreshToken == null || !storedRefreshToken.getRefreshToken().equals(requestRefreshToken)) {
            sendErrorResponse(response, JwtErrorCode.LOGOUT_FAIL_MALFORMED_TOKEN);
            return;
        }

        try {
            jwtProvider.deleteRefreshTokenFromDB(customUser.getId());
            log.info("Refresh token for user's ID '{}' has been deleted from the database.", customUser.getId());
        } catch (Exception e) {
            log.error("Failed to delete refresh token for user with ID '{}': '{}'", customUser.getId(), e.getMessage());
            sendErrorResponse(response, e);
            return;
        }

        sendSuccessResponse(response);
    }

    private boolean validRefreshToken(String refreshToken) {
        try {
            return jwtProvider.validToken(refreshToken, JwtProvider.REFRESH_TOKEN_NAME);
        } catch (JwtException.JwtNotValidateException e) {
            return false;
        }
    }

    private void sendSuccessResponse(HttpServletResponse response) throws IOException {
        ApiResponse<?> apiResponse = ApiResponse.ok();
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }

    private void sendErrorResponse(HttpServletResponse response, JwtErrorCode jwtErrorCode) throws IOException {
        ApiResponse<?> apiResponse = ApiResponse.fail(jwtErrorCode, null);
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }

    private void sendErrorResponse(HttpServletResponse response, Exception exception) throws IOException {
        ApiResponse<?> apiResponse = ApiResponse.fail(exception.getMessage(), null);
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}
