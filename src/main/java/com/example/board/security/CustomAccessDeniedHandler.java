// 수정: src/main/java/com/example/board/security/CustomAccessDeniedHandler.java
package com.example.board.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

import com.example.board.dto.ErrorResponse;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                      HttpServletResponse response,
                      AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        // 상세 로깅
        log.warn("Access Denied - User: {}, URI: {}, Method: {}, IP: {}, Message: {}",
                getCurrentUsername(),
                request.getRequestURI(),
                request.getMethod(),
                request.getRemoteAddr(),
                accessDeniedException.getMessage());

        // URL에 따라 에러 메시지 결정
        String errorMessage = determineErrorMessage(request);

        if (isAjaxRequest(request)) {
            handleAjaxRequest(request, response, errorMessage);
        } else {
            // 에러 메시지를 세션에 저장
            request.getSession().setAttribute("errorMessage", errorMessage);
            handleWebRequest(request, response);
        }
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        return "anonymous";
    }

    private String determineErrorMessage(HttpServletRequest request) {
        String uri = request.getRequestURI();

        if (uri.startsWith("/admin")) {
            return "관리자만 접근할 수 있습니다";
        } else if (uri.contains("/edit") || uri.contains("/delete")) {
            return "본인이 작성한 글만 수정/삭제할 수 있습니다";
        } else {
            return "이 페이지에 접근할 권한이 없습니다";
        }
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With")) ||
               request.getRequestURI().startsWith("/api/");
    }

    // 파라미터 변경: request 추가 (path, timestamp용)
    private void handleAjaxRequest(HttpServletRequest request,
                                   HttpServletResponse response,
                                   String errorMessage)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("Access Denied")
                .message(errorMessage)
                .status(403)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    private void handleWebRequest(HttpServletRequest request,
                                  HttpServletResponse response)
            throws IOException {
        request.getSession().setAttribute("PREVIOUS_URL", request.getRequestURI());
        response.sendRedirect("/error/403");
    }
}
