// 새 파일: src/main/java/com/example/board/controller/FragmentController.java
package com.example.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/fragments")
public class FragmentController {

    // GET /fragments/demo - Fragment 데모 페이지
    @GetMapping("/demo")
    public String demo(Model model) {

        // 1. Alert용 데이터
        model.addAttribute("successMessage", "회원가입이 완료되었습니다!");
        model.addAttribute("warningMessage", "비밀번호를 확인해주세요.");
        model.addAttribute("errorMessage", "로그인에 실패했습니다.");

        // 2. Card용 데이터
        model.addAttribute("cardTitle", "공지사항");
        model.addAttribute("cardContent", "시스템 점검이 예정되어 있습니다.");

        // 3. Pagination용 데이터
        model.addAttribute("currentPage", 3);
        model.addAttribute("totalPages", 10);

        return "fragments/demo";
    }
}
