// 새 파일: src/main/java/com/example/board/controller/MyPageController.java
package com.example.board.controller;

import com.example.board.dto.BoardListResponse;
import com.example.board.dto.MyCommentResponse;  // 02. 내 게시글 및 댓글에서 생성
import com.example.board.security.CustomUserDetails;
import com.example.board.service.BoardService;
import com.example.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MyPageController {

    private final BoardService boardService;
    private final CommentService commentService;

    // GET /members/my-boards - 내 게시글 목록 조회
    @GetMapping("/my-boards")
    public String myBoards(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdAt",
                    direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        // BoardService.findByMemberId() - 01장/02에서 추가
        Page<BoardListResponse> boards =
                boardService.findByMemberId(userDetails.getMemberId(), pageable);
        model.addAttribute("boards", boards);
        return "members/my-boards";
    }

    // GET /members/my-comments - 내 댓글 목록 조회
    @GetMapping("/my-comments")
    public String myComments(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdAt",
                    direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        // CommentService.findByMemberIdWithBoard() - 02. 내 게시글 및 댓글에서 추가
        Page<MyCommentResponse> comments =
                commentService.findByMemberIdWithBoard(
                        userDetails.getMemberId(), pageable);
        model.addAttribute("comments", comments);
        return "members/my-comments";
    }
}
