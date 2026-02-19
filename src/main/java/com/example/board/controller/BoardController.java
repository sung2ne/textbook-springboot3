// 수정: src/main/java/com/example/board/controller/BoardController.java
package com.example.board.controller;

import com.example.board.dto.BoardForm;
import com.example.board.dto.BoardDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    // GET /boards - 게시글 목록 조회
    @GetMapping
    public String list(Model model) {
        // 더미 데이터 생성 (Part 2에서 실제 DB 조회로 대체)
        List<BoardDto> boards = Arrays.asList(
            new BoardDto(1L, "첫 번째 게시글", "홍길동", LocalDateTime.now()),
            new BoardDto(2L, "두 번째 게시글", "김철수", LocalDateTime.now().minusDays(1)),
            new BoardDto(3L, "세 번째 게시글", "이영희", LocalDateTime.now().minusDays(2))
        );

        model.addAttribute("boards", boards);
        return "boards/list";
    }

    // GET /boards/new - 새 글 작성 폼 표시
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("boardForm", new BoardForm());
        return "boards/form";
    }

    // POST /boards - 새 글 등록 처리
    @PostMapping
    public String create(@Valid @ModelAttribute("boardForm") BoardForm form,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {

        // 검증 오류가 있으면 폼으로 다시 이동
        if (bindingResult.hasErrors()) {
            return "boards/form";
        }

        // 입력값 확인 (디버깅용)
        System.out.println("제목: " + form.getTitle());
        System.out.println("내용: " + form.getContent());
        System.out.println("작성자: " + form.getWriter());

        // TODO: 실제 DB 저장 로직은 Part 2에서 추가

        redirectAttributes.addFlashAttribute("message", "저장되었습니다");
        return "redirect:/boards";
    }
}
