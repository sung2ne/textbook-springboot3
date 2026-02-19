// 수정: src/main/java/com/example/board/controller/CommentApiController.java
package com.example.board.controller;

import com.example.board.dto.CommentForm;
import com.example.board.dto.CommentResponse;
import com.example.board.security.CustomUserDetails;
import com.example.board.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentApiController {

    private final CommentService commentService;

    // GET /api/boards/{boardId}/comments - 댓글 목록 조회 (09장에서 작성)
    @GetMapping("/boards/{boardId}/comments")
    public ResponseEntity<List<CommentResponse>> list(@PathVariable Long boardId) {
        List<CommentResponse> comments = commentService.findByBoardId(boardId);
        return ResponseEntity.ok(comments);
    }

    // POST /api/boards/{boardId}/comments - 댓글 작성 (인증 필요)
    @PostMapping("/boards/{boardId}/comments")
    public ResponseEntity<?> create(@PathVariable Long boardId,
                                    @Valid @RequestBody CommentForm form,
                                    BindingResult bindingResult,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 비로그인 사용자 체크
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요합니다"));
        }

        // 검증 오류
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errors);
        }

        CommentResponse comment = commentService.save(boardId, form, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    // PUT /api/comments/{commentId} - 댓글 수정 (권한 필요)
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<?> update(@PathVariable Long commentId,
                                    @Valid @RequestBody CommentForm form,
                                    BindingResult bindingResult,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 비로그인 사용자 체크
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요합니다"));
        }

        // 검증 오류
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            CommentResponse comment = commentService.update(
                    commentId, form,
                    userDetails.getUsername(),
                    userDetails.getMember().getRole());
            return ResponseEntity.ok(comment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // DELETE /api/comments/{commentId} - 댓글 삭제 (권한 필요)
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> delete(@PathVariable Long commentId,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 비로그인 사용자 체크
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요합니다"));
        }

        try {
            commentService.delete(
                    commentId,
                    userDetails.getUsername(),
                    userDetails.getMember().getRole());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "댓글이 삭제되었습니다"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
