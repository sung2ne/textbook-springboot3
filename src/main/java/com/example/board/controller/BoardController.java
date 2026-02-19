// 수정: src/main/java/com/example/board/controller/BoardController.java (메서드 추가)
package com.example.board.controller;

import com.example.board.dto.BoardForm;
import com.example.board.dto.BoardListResponse;
import com.example.board.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // GET /boards - 게시글 목록 조회 (03장에서 작성)
    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(required = false) String keyword,
                       Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<BoardListResponse> boardPage = boardService.search(keyword, pageable);

        model.addAttribute("boards", boardPage.getContent());
        model.addAttribute("page", boardPage);
        model.addAttribute("keyword", keyword);

        return "boards/list";
    }

    // GET /boards/new - 게시글 작성 폼 표시 (추가)
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("boardForm", new BoardForm());
        return "boards/form";
    }

    // POST /boards - 게시글 등록 (추가)
    @PostMapping
    public String create(@Valid @ModelAttribute BoardForm form,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {

        // writerName은 등록 시에만 필수 (수정 시 disabled로 전송 안됨)
        if (form.getWriterName() == null || form.getWriterName().isBlank()) {
            bindingResult.rejectValue("writerName", "NotBlank", "작성자를 입력해주세요.");
        }

        // 검증 오류가 있으면 폼 다시 표시
        if (bindingResult.hasErrors()) {
            return "boards/form";
        }

        // 저장
        Long boardId = boardService.save(form);

        // 성공 메시지
        redirectAttributes.addFlashAttribute("message", "게시글이 등록되었습니다.");

        // 상세 페이지로 리다이렉트
        return "redirect:/boards/" + boardId;
    }
}
