package com.example.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/conditional")
public class ConditionalController {

    // GET /conditional/demo - 조건문 데모 페이지
    @GetMapping("/demo")
    public String demo(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int score,
            Model model) {

        // 1. 사용자 정보 (null 가능)
        model.addAttribute("username", username);

        // 2. 역할 (ADMIN, USER, GUEST 중 하나)
        model.addAttribute("role", role != null ? role : "GUEST");

        // 3. 점수 (등급 판정용)
        model.addAttribute("score", score);

        // 4. 불린 값
        model.addAttribute("isLoggedIn", username != null);
        model.addAttribute("isPremium", score >= 80);

        return "conditional/demo";
    }
}
