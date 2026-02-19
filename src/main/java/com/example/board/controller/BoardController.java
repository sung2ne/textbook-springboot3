// 수정: src/main/java/com/example/board/controller/BoardController.java
package com.example.board.controller;

import com.example.board.dto.AttachmentResponse;
import com.example.board.dto.BoardDetailResponse;
import com.example.board.dto.BoardForm;
import com.example.board.dto.BoardListResponse;
import com.example.board.security.CurrentMember;
import com.example.board.security.CustomUserDetails;
import com.example.board.domain.Member;
import com.example.board.service.AttachmentService;
import com.example.board.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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

    // GET /boards - 게시글 목록 조회 (03장에서 작성)
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

        // 로그인 상태 전달 (Thymeleaf에서 사용) - 08장에서 추가
        if (userDetails != null) {
            model.addAttribute("currentUsername", userDetails.getUsername());
        }

        return "boards/list";
    }

    // GET /boards/{id} - 게시글 상세 조회 (05장에서 작성)
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
                         @AuthenticationPrincipal CustomUserDetails userDetails,
                         Model model) {
        BoardDetailResponse board = boardService.findById(id);
        model.addAttribute("board", board);

        // 본인 글인지 확인 (수정/삭제 버튼 표시용) - 08장에서 추가
        if (userDetails != null) {
            boolean isOwner = boardService.isOwner(id, userDetails.getUsername());
            boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

            model.addAttribute("isOwner", isOwner);
            model.addAttribute("isAdmin", isAdmin);
        }

        return "boards/detail";
    }

    // GET /boards/new - 게시글 작성 폼 표시 (04장에서 작성)
    @GetMapping("/new")
    public String createForm(@CurrentMember Member member, Model model) {
        model.addAttribute("boardForm", new BoardForm());
        model.addAttribute("writerName", member.getName());
        return "boards/form";
    }

    // POST /boards - 게시글 등록 (04장에서 작성, 10장에서 파일 업로드 추가)
    @PostMapping
    public String create(@Valid @ModelAttribute BoardForm form,
                         BindingResult bindingResult,
                         @RequestParam(required = false) List<MultipartFile> files,
                         @AuthenticationPrincipal CustomUserDetails userDetails,
                         RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "boards/form";
        }

        Long boardId = boardService.save(form, userDetails.getUsername(), files);

        redirectAttributes.addFlashAttribute("message", "게시글이 등록되었습니다.");

        return "redirect:/boards/" + boardId;
    }

    // GET /boards/{id}/edit - 게시글 수정 폼 표시 (06장에서 작성)
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           @AuthenticationPrincipal CustomUserDetails userDetails,
                           Model model) {
        // 본인 글인지 확인 - 08장에서 추가
        if (!boardService.isOwner(id, userDetails.getUsername())) {
            throw new IllegalArgumentException("수정 권한이 없습니다");
        }

        BoardForm form = boardService.getFormById(id);
        List<AttachmentResponse> attachments = attachmentService.findByBoardId(id)
                .stream()
                .map(AttachmentResponse::new)
                .collect(Collectors.toList());

        model.addAttribute("boardForm", form);
        model.addAttribute("attachments", attachments);
        return "boards/form";
    }

    // POST /boards/{id} - 게시글 수정 처리 (06장에서 작성, 10장에서 파일 업로드 추가)
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute BoardForm form,
                         BindingResult bindingResult,
                         @RequestParam(required = false) List<MultipartFile> files,
                         @AuthenticationPrincipal CustomUserDetails userDetails,
                         RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "boards/form";
        }

        boardService.update(id, form, userDetails.getUsername(), files);

        redirectAttributes.addFlashAttribute("message", "게시글이 수정되었습니다.");

        return "redirect:/boards/" + id;
    }

    // DELETE /boards/{id} - 게시글 삭제 - 변경
    // @PreAuthorize가 권한을 체크하므로 isAdmin 파라미터 불필요
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        boardService.delete(id, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    // 예외 처리 - 추가
    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied() {
        return "redirect:/error/403";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleNotFound() {
        return "redirect:/error/404";
    }
}
