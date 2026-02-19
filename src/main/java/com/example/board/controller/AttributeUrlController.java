package com.example.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/attribute-url")
public class AttributeUrlController {

    // GET /attribute-url/demo - 속성 설정과 URL 데모 페이지
    @GetMapping("/demo")
    public String demo(
            @RequestParam(defaultValue = "home") String tab,
            @RequestParam(defaultValue = "1") int page,
            Model model) {

        // 1. 현재 탭
        model.addAttribute("currentTab", tab);

        // 2. 페이지 정보
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", 5);

        // 3. 게시글 ID 목록 (링크 생성용)
        List<Long> boardIds = Arrays.asList(1L, 2L, 3L, 4L, 5L);
        model.addAttribute("boardIds", boardIds);

        // 4. 카테고리 목록 (셀렉트 박스용)
        List<String> categories = Arrays.asList("공지사항", "자유게시판", "Q&A", "자료실");
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategory", "자유게시판");

        // 5. 체크박스 상태
        model.addAttribute("agreeTerms", true);
        model.addAttribute("agreeMarketing", false);

        // 6. 버튼 활성화 상태
        model.addAttribute("canEdit", true);
        model.addAttribute("canDelete", false);

        return "attribute-url/demo";
    }

    // GET /attribute-url/board/{id} - 게시글 상세 (링크 테스트용)
    @GetMapping("/board/{id}")
    public String boardDetail(@PathVariable Long id, Model model) {
        model.addAttribute("boardId", id);
        model.addAttribute("message", "게시글 " + id + "번의 상세 페이지입니다.");
        return "attribute-url/board-detail";
    }
}
