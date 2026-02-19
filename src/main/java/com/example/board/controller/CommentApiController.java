// 새 파일: src/main/java/com/example/board/controller/CommentApiController.java
package com.example.board.controller;

import com.example.board.dto.CommentForm;
import com.example.board.dto.CommentResponse;
import com.example.board.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    // GET /api/boards/{boardId}/comments - 댓글 목록 조회
    @GetMapping("/boards/{boardId}/comments")
    public ResponseEntity<List<CommentResponse>> list(@PathVariable Long boardId) {
        List<CommentResponse> comments = commentService.findByBoardId(boardId);
        return ResponseEntity.ok(comments);
    }

    // POST /api/boards/{boardId}/comments - 댓글 작성
    @PostMapping("/boards/{boardId}/comments")
    public ResponseEntity<?> create(@PathVariable Long boardId,
                                    @Valid @RequestBody CommentForm form,
                                    BindingResult bindingResult) {

        // 검증 오류
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errors);
        }

        CommentResponse comment = commentService.save(boardId, form);
        return ResponseEntity.ok(comment);
    }

    // DELETE /api/comments/{id} - 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Long commentId) {
        commentService.delete(commentId);
        return ResponseEntity.ok().build();
    }

    // PUT /api/comments/{id} - 댓글 수정
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<?> update(@PathVariable Long commentId,
                                    @Valid @RequestBody CommentForm form,
                                    BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errors);
        }

        CommentResponse comment = commentService.update(commentId, form);
        return ResponseEntity.ok(comment);
    }
}
