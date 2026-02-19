package com.example.board.dto;

import com.example.board.domain.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponse {

    private final Long id;
    private final String content;
    private final String writerName;
    private final Long writerId;
    private final LocalDateTime createdAt;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.writerName = comment.getWriterName();
        this.writerId = comment.getMember() != null ? comment.getMember().getId() : null;
        this.createdAt = comment.getCreatedAt();
    }
}
