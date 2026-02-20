// 수정: src/main/java/com/example/board/controller/LoginController.java
package com.example.board.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage(
        @RequestParam(value = "error", required = false) String error,
        Model model,
        HttpSession session
    ) {
        if (error != null) {
            model.addAttribute("error", "아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        // 보호된 페이지에서 리다이렉트된 경우 메시지 표시 후 세션 정리
        if (session.getAttribute("REDIRECT_URL") != null) {
            model.addAttribute("loginRequired", true);
            session.removeAttribute("REDIRECT_URL");
        }

        return "login";
    }
}
