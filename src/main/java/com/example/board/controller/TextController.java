// 새 파일: src/main/java/com/example/board/controller/TextController.java
package com.example.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/text")
public class TextController {

    // GET /text/demo - 텍스트 출력 데모 페이지
    @GetMapping("/demo")
    public String demo(
            @RequestParam(defaultValue = "홍길동") String name,
            Model model) {

        // 1. 일반 텍스트
        model.addAttribute("name", name);
        model.addAttribute("greeting", "안녕하세요!");
        model.addAttribute("description", "Thymeleaf 텍스트 출력 예제입니다.");

        // 2. HTML 태그가 포함된 텍스트 (th:text vs th:utext 비교용)
        model.addAttribute("htmlContent", "<strong>중요한</strong> 공지사항입니다.");
        model.addAttribute("boldText", "<b>굵은 글씨</b>와 <i>이탤릭</i> 테스트");

        // 3. 특수문자가 포함된 텍스트
        model.addAttribute("specialChars", "가격: 10,000원 & 할인율: 20%");
        model.addAttribute("scriptTag", "<script>alert('XSS 공격!')</script>");

        // 4. 여러 줄 텍스트
        model.addAttribute("multiLine", "첫 번째 줄\n두 번째 줄\n세 번째 줄");

        // 5. 숫자와 날짜
        model.addAttribute("price", 15000);
        model.addAttribute("currentTime", java.time.LocalDateTime.now());

        return "text/demo";
    }
}
