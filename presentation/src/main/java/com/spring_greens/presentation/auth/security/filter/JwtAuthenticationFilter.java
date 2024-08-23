package com.spring_greens.presentation.auth.security.filter;

import com.spring_greens.presentation.auth.exception.JwtException;
import com.spring_greens.presentation.auth.security.provider.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthenticationFilter is a filter that validates JWT tokens in HTTP requests.
 * <p>
 * This filter ensures that only requests with valid tokens are processed,
 * while requests with invalid tokens are handled by setting an appropriate error code.
 * </p>
 * <p>
 * including:
 * <ul>
 *     <li>Extracts the JWT token from the Authorization header of the request.</li>
 *     <li>Validates the extracted token.</li>
 *     <li>Sets authentication information in the security context if the token is valid.</li>
 *     <li>Sets an error code on the request if the token is invalid.</li>
 * </ul>
 * </p>
 *
 * @author 01223lsh
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    private static final String WEBSOCKET_HANDSHAKE_PATH = "/ws";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


//
        // Check if the request is a WebSocket handshake
        if (isWebSocketHandshake(request)) {
            log.info("Pass Socket");
            // Bypass this filter for WebSocket connections
            filterChain.doFilter(request, response);
            return;
        }

        String token = getAccessToken(request);

        log.info("checking token : {}", token);
        try {
            // 여기가 토큰을 받고, 검증하는 부분이 되겠네
            if(jwtProvider.validToken(token, JwtProvider.Access_TOKEN_NAME)) {
                Authentication authentication = jwtProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtException.JwtNotValidateException e) {
            request.setAttribute("jwtErrorCode", e.getJwtErrorCode());
        }

        filterChain.doFilter(request, response);
    }

    private String getAccessToken(HttpServletRequest request) {

        log.info("Http get Token");
        String authorizationHeader = request.getHeader(JwtProvider.HEADER_AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith(JwtProvider.TOKEN_PREFIX)) {
            return authorizationHeader.substring(JwtProvider.TOKEN_PREFIX.length());
        }

        return null;

    }

    private boolean isWebSocketHandshake(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        // Check if the URI matches your WebSocket endpoint
        return requestURI.startsWith("/ws");
    }
}