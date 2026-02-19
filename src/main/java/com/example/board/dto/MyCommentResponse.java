// 새 파일: src/main/java/com/example/board/dto/MyCommentResponse.java
package com.example.board.dto;

import com.example.board.domain.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MyCommentResponse {

    private final Long id;
    private final String content;
    private final Long boardId;
    private final String boardTitle;
    private final LocalDateTime createdAt;

    public MyCommentResponse(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.boardId = comment.getBoard().getId();
        this.boardTitle = comment.getBoard().getTitle();
        this.createdAt = comment.getCreatedAt();
    }
}
