package com.spring_greens.presentation.auth.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring_greens.presentation.global.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handles authentication failures in Spring Security.
 * <p>
 * Logs failure details and sends a JSON response with the failure message.
 * </p>
 *
 * @author 01223lsh
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class CustomFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        /** 요청 시간 만료 시 타는 것으로 확인. 예외 처리 추가 예정*/
        log.info("Authentication failed from IP: {} | URI: {} | Reason: {}", request.getRemoteAddr(), request.getRequestURI(), exception.getMessage());
        ApiResponse<?> apiResponse = ApiResponse.fail(exception.getMessage(), null);
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}
