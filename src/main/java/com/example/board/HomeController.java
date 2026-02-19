// 새 파일: src/main/java/com/example/board/HomeController.java
package com.example.board;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "Hello, Thymeleaf!");
        return "home";  // templates/home.html을 찾아 렌더링
    }
}
