// 새 파일: src/main/java/com/example/board/controller/ErrorPageController.java
package com.example.board.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/error")
public class ErrorPageController {

    @GetMapping("/403")
    public String forbidden(HttpServletRequest request, Model model) {
        log.warn("403 Error - URI: {}, IP: {}",
                request.getAttribute("PREVIOUS_URL"),
                request.getRemoteAddr());

        // 세션에서 에러 메시지 가져오기
        String errorMessage = (String) request.getSession().getAttribute("errorMessage");
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            request.getSession().removeAttribute("errorMessage");
        } else {
            model.addAttribute("errorMessage", "접근 권한이 없습니다");
        }

        return "error/403";
    }

    @GetMapping("/401")
    public String unauthorized(Model model) {
        model.addAttribute("errorMessage", "로그인이 필요합니다");
        return "error/401";
    }

    @GetMapping("/404")
    public String notFound(Model model) {
        model.addAttribute("errorMessage", "페이지를 찾을 수 없습니다");
        return "error/404";
    }

    @GetMapping("/500")
    public String serverError(Model model) {
        model.addAttribute("errorMessage", "서버 오류가 발생했습니다");
        return "error/500";
    }
}
