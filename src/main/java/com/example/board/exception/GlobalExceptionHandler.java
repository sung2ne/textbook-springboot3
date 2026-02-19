// 새 파일: src/main/java/com/example/board/exception/GlobalExceptionHandler.java
package com.example.board.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // 권한 없음 예외 (Controller에서 발생한 경우)
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied(AccessDeniedException e,
                                     RedirectAttributes redirectAttributes) {
        log.warn("Access denied: {}", e.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", "접근 권한이 없습니다");
        return "redirect:/error/403";
    }

    // 잘못된 요청
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException e,
                                        RedirectAttributes redirectAttributes) {
        log.warn("Illegal argument: {}", e.getMessage());
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        return "redirect:/boards";
    }

    // 리소스를 찾을 수 없음
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFound(ResourceNotFoundException e,
                                         RedirectAttributes redirectAttributes) {
        log.warn("Resource not found: {}", e.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/error/404";
    }

    // AJAX 요청 예외 처리
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleAjaxException(RuntimeException e,
                                                  jakarta.servlet.http.HttpServletRequest request) {
        if (isAjaxRequest(request)) {
            log.error("AJAX request error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }

        // 일반 요청은 다시 throw (Spring의 기본 에러 처리 사용)
        throw e;
    }

    // 일반적인 예외
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception e, RedirectAttributes redirectAttributes) {
        log.error("Unexpected error occurred", e);
        redirectAttributes.addFlashAttribute("errorMessage", "오류가 발생했습니다");
        return "redirect:/error/500";
    }

    private boolean isAjaxRequest(jakarta.servlet.http.HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }
}
