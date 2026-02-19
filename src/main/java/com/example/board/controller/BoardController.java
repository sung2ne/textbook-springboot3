package com.example.board.controller;

import com.example.board.dto.AttachmentResponse;
import com.example.board.dto.BoardDetailResponse;
import com.example.board.dto.BoardForm;
import com.example.board.dto.BoardListResponse;
import com.example.board.service.AttachmentService;
import com.example.board.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final AttachmentService attachmentService;

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

    // GET /boards/{id} - 게시글 상세 조회 (05장에서 작성)
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        BoardDetailResponse board = boardService.findById(id);
        model.addAttribute("board", board);
        return "boards/detail";
    }

    // GET /boards/new - 게시글 작성 폼 표시 (04장에서 작성)
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("boardForm", new BoardForm());
        return "boards/form";
    }

    // POST /boards - 게시글 등록 (04장에서 작성, 10장 03절에서 파일 업로드 추가)
    @PostMapping
    public String create(@Valid @ModelAttribute BoardForm form,
                         BindingResult bindingResult,
                         @RequestParam(required = false) List<MultipartFile> files,
                         RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "boards/form";
        }

        Long boardId = boardService.save(form, files);

        redirectAttributes.addFlashAttribute("message", "게시글이 등록되었습니다.");

        return "redirect:/boards/" + boardId;
    }

    // GET /boards/{id}/edit - 게시글 수정 폼 표시 (06장에서 작성, 첨부파일 목록 추가)
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        BoardForm form = boardService.getFormById(id);
        List<AttachmentResponse> attachments = attachmentService.findByBoardId(id)
                .stream()
                .map(AttachmentResponse::new)
                .collect(Collectors.toList());

        model.addAttribute("boardForm", form);
        model.addAttribute("attachments", attachments);
        return "boards/form";
    }

    // POST /boards/{id} - 게시글 수정 처리 (06장에서 작성, 파일 업로드 추가)
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute BoardForm form,
                         BindingResult bindingResult,
                         @RequestParam(required = false) List<MultipartFile> files,
                         RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "boards/form";
        }

        boardService.update(id, form, files);

        redirectAttributes.addFlashAttribute("message", "게시글이 수정되었습니다.");

        return "redirect:/boards/" + id;
    }

    // DELETE /boards/{id} - 게시글 삭제 (07장에서 추가)
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boardService.delete(id);
        return ResponseEntity.ok().build();
    }
}
