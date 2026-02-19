// 수정: src/main/java/com/example/board/HomeController.java
package com.example.board;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(
        @RequestParam(value = "logout", required = false) String logout,
        Model model
    ) {
        if (logout != null) {
            model.addAttribute("message", "로그아웃되었습니다.");
        }

        return "home";
    }
}
