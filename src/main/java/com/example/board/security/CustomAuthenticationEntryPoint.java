// 수정: src/main/java/com/example/board/security/CustomAuthenticationEntryPoint.java
package com.example.board.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                        HttpServletResponse response,
                        AuthenticationException authException)
            throws IOException, ServletException {

        log.warn("Unauthorized access: {} - {}",
                request.getRequestURI(),
                authException.getClass().getSimpleName());

        String errorMessage = determineErrorMessage(authException);

        if (isAjaxRequest(request)) {
            handleAjaxRequest(response, errorMessage);
        } else {
            request.getSession().setAttribute("loginError", errorMessage);
            saveTargetUrl(request);
            response.sendRedirect("/login");
        }
    }

    private String determineErrorMessage(AuthenticationException exception) {
        if (exception instanceof BadCredentialsException) {
            return "이메일 또는 비밀번호가 올바르지 않습니다";
        } else if (exception instanceof DisabledException) {
            return "비활성화된 계정입니다";
        } else if (exception instanceof LockedException) {
            return "잠긴 계정입니다. 관리자에게 문의하세요";
        } else if (exception instanceof AccountExpiredException) {
            return "만료된 계정입니다";
        } else if (exception instanceof CredentialsExpiredException) {
            return "비밀번호가 만료되었습니다. 비밀번호를 변경해주세요";
        } else {
            return "로그인이 필요합니다";
        }
    }

    private void saveTargetUrl(HttpServletRequest request) {
        String targetUrl = request.getRequestURI();
        if (request.getQueryString() != null) {
            targetUrl += "?" + request.getQueryString();
        }
        request.getSession().setAttribute("REDIRECT_URL", targetUrl);
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With")) ||
               request.getRequestURI().startsWith("/api/");
    }

    private void handleAjaxRequest(HttpServletResponse response,
                                   String errorMessage)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("error", "Unauthorized");
        errorDetails.put("message", errorMessage);
        errorDetails.put("status", 401);

        response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
    }

    private void handleWebRequest(HttpServletRequest request,
                                  HttpServletResponse response)
            throws IOException {
        // HTML 페이지 요청만 REDIRECT_URL 저장 (favicon, 이미지 등 정적 리소스 제외)
        String acceptHeader = request.getHeader("Accept");
        if (acceptHeader != null && acceptHeader.contains("text/html")) {
            String targetUrl = request.getRequestURI();
            if (request.getQueryString() != null) {
                targetUrl += "?" + request.getQueryString();
            }
            request.getSession().setAttribute("REDIRECT_URL", targetUrl);
        }

        response.sendRedirect("/login");
    }
}
