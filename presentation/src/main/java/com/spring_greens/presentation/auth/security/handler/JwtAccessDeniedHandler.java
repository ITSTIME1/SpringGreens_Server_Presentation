package com.spring_greens.presentation.auth.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring_greens.presentation.global.api.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
/**
 * Handles Authorization - access denied (403) errors.
 * <p>
 * Implements {@link AccessDeniedHandler} to log access denial details
 * and send a JSON response with the error message when a user lacks
 * permission for a resource.
 * </p>
 *
 * @author 01223lsh
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        /** 403 권한 거부 에러를 뱉는다. | 프론트에서 사용자 권한 부족 Alert 표출 필요 | 권한 부족 관련 Exception Handler로 기능 추가 가능 */
        log.info("Authentication failed from IP: {} | URI: {} | Reason: {}", request.getRemoteAddr(), request.getRequestURI(), accessDeniedException.getMessage());
        ApiResponse<?> apiResponse = ApiResponse.fail(accessDeniedException.getMessage(), null);
        objectMapper.writeValue(response.getWriter(), apiResponse);
//        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }
}
