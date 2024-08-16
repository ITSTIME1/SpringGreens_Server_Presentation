package com.spring_greens.presentation.auth.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring_greens.presentation.auth.config.JwtProperties;
import com.spring_greens.presentation.auth.dto.CustomUser;
import com.spring_greens.presentation.auth.dto.response.TokenResponse;
import com.spring_greens.presentation.auth.security.provider.JwtProvider;
import com.spring_greens.presentation.auth.util.CookieUtil;
import com.spring_greens.presentation.global.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handles successful authentication events by generating and providing JWT tokens.
 * <p>
 * This component is invoked when a user successfully authenticates.
 * </p>
 * <p>
 * including:
 * <ul>
 *     <li>Generates JWT access and refresh tokens.</li>
 *     <li>Sets the refresh token in an HTTP-only cookie with an expiration time matching the refresh token's expiration.</li>
 *     <li>Sends the access token in the response body.</li>
 * </ul>
 * </p>
 *
 * @author 01223lsh
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;
    private final ObjectMapper objectMapper;

    /**
     * Called when authentication is successful.
     * <p>
     * Generates JWT tokens, sets the refresh token in a cookie, and sends the access token in the response.
     * </p>
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        createTokensAndSetResponse(request, response, customUser);
        // Optional redirection or attribute clearing can be handled here
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
     * Creates JWT tokens and sets them in the response.
     * <p>
     * Generates an access token and a refresh token. The access token is included in the response body,
     * while the refresh token is stored or updated in the database and added to an HTTP-only cookie.
     * The cookie's expiration time matches the refresh token's expiration time.
     * </p>
     */
    private void createTokensAndSetResponse(HttpServletRequest request, HttpServletResponse response, CustomUser customUser) throws IOException {
        String accessToken = jwtProvider.generateAccessToken(customUser);
        String refreshToken = jwtProvider.generateRefreshToken(customUser);

        addRefreshTokenToCookie(request, response, refreshToken);

        TokenResponse tokenResponse = TokenResponse.builder()
                .access_token(accessToken)
                .build();
        ApiResponse<TokenResponse> apiResponse = ApiResponse.ok(tokenResponse);
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }

    /* 로그인 완료 후 쿠키 날려주는 거 필요할까? */
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        //super.clearAuthenticationAttributes(request);
//        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    /* Server에서 Redirect 시 필요 */
    /*
    private String getTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                .queryParam("token", token)
                .build()
                .toUriString();
    }*/
}