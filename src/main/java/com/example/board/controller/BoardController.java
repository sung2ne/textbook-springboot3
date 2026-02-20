// 수정: src/main/java/com/example/board/controller/BoardController.java
package com.example.board.controller;

import com.example.board.domain.Role;
import com.example.board.dto.AttachmentResponse;
import com.example.board.dto.BoardDetailResponse;
import com.example.board.dto.BoardForm;
import com.example.board.dto.BoardListResponse;
import com.example.board.security.CustomUserDetails;
import com.example.board.service.AttachmentService;
import com.example.board.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    // GET /boards - 게시글 목록 조회 ('내 글' 뱃지용 currentUsername 추가)
    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(required = false) String keyword,
                       @AuthenticationPrincipal CustomUserDetails userDetails,
                       Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<BoardListResponse> boardPage = boardService.search(keyword, pageable);

        model.addAttribute("boards", boardPage.getContent());
        model.addAttribute("page", boardPage);
        model.addAttribute("keyword", keyword);

        // 현재 로그인 사용자명 전달 ('내 글' 뱃지용)
        if (userDetails != null) {
            model.addAttribute("currentUsername", userDetails.getUsername());
        }

        return "boards/list";
    }

    // GET /boards/{id} - 게시글 상세 조회 (권한 정보 추가)
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
                         @AuthenticationPrincipal CustomUserDetails userDetails,
                         Model model) {
        BoardDetailResponse board = boardService.findByIdWithComments(id);
        model.addAttribute("board", board);

        // 수정/삭제 권한 체크
        if (userDetails != null) {
            boolean isOwner = boardService.isOwner(id, userDetails.getUsername());
            boolean isAdmin = userDetails.getMember().getRole() == Role.ADMIN;
            model.addAttribute("canEdit", isOwner || isAdmin);
            model.addAttribute("isOwner", isOwner);
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("currentUsername", userDetails.getUsername());
        } else {
            model.addAttribute("canEdit", false);
            model.addAttribute("isOwner", false);
            model.addAttribute("isAdmin", false);
        }

        return "boards/detail";
    }

    // GET /boards/new - 게시글 작성 폼 표시 (인증 필요)
    @GetMapping("/new")
    public String createForm(@AuthenticationPrincipal CustomUserDetails userDetails,
                             Model model) {
        model.addAttribute("boardForm", new BoardForm());
        model.addAttribute("writerName", userDetails.getName());
        return "boards/form";
    }

    // POST /boards - 게시글 등록 (인증 기반)
    @PostMapping
    public String create(@Valid @ModelAttribute BoardForm form,
                         BindingResult bindingResult,
                         @RequestParam(required = false) List<MultipartFile> files,
                         @AuthenticationPrincipal CustomUserDetails userDetails,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("writerName", userDetails.getName());
            return "boards/form";
        }

        // 인증된 사용자의 username으로 게시글 생성
        Long boardId = boardService.save(form, userDetails.getUsername(), files);

        redirectAttributes.addFlashAttribute("message", "게시글이 등록되었습니다.");

        return "redirect:/boards/" + boardId;
    }

    // GET /boards/{id}/edit - 게시글 수정 폼 표시 (권한 필요)
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           @AuthenticationPrincipal CustomUserDetails userDetails,
                           Model model) {
        // 권한 체크
        checkEditPermission(id, userDetails);

        BoardForm form = boardService.getFormById(id);
        // 기존 첨부파일 목록 - 10장/05절에서 추가
        List<AttachmentResponse> attachments = attachmentService.findByBoardId(id)
                .stream()
                .map(AttachmentResponse::new)
                .collect(Collectors.toList());
        model.addAttribute("boardForm", form);
        model.addAttribute("writerName", form.getWriterName());
        model.addAttribute("attachments", attachments);
        return "boards/form";
    }

    // POST /boards/{id} - 게시글 수정 처리 (권한 필요)
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute BoardForm form,
                         BindingResult bindingResult,
                         @RequestParam(required = false) List<MultipartFile> files,
                         @AuthenticationPrincipal CustomUserDetails userDetails,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        // 권한 체크
        checkEditPermission(id, userDetails);

        if (bindingResult.hasErrors()) {
            model.addAttribute("writerName", boardService.getFormById(id).getWriterName());
            return "boards/form";
        }

        boardService.update(id, form, files);

        redirectAttributes.addFlashAttribute("message", "게시글이 수정되었습니다.");

        return "redirect:/boards/" + id;
    }

    // DELETE /boards/{id} - 게시글 삭제 (권한 필요)
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        // 권한 체크
        checkEditPermission(id, userDetails);

        boardService.delete(id);
        return ResponseEntity.ok().build();
    }

    // === Private Helper Methods ===

    private void checkEditPermission(Long boardId, CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalArgumentException("로그인이 필요합니다");
        }

        boolean isOwner = boardService.isOwner(boardId, userDetails.getUsername());
        boolean isAdmin = userDetails.getMember().getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new IllegalArgumentException("수정/삭제 권한이 없습니다");
        }
    }
}
