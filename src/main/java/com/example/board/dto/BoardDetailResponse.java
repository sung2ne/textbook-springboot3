package com.example.board.dto;

import com.example.board.domain.Board;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardDetailResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final String writerName;
    private final Long writerId;
    private final int viewCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public BoardDetailResponse(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.writerName = board.getWriterName();
        this.writerId = board.getMember() != null ? board.getMember().getId() : null;
        this.viewCount = board.getViewCount();
        this.createdAt = board.getCreatedAt();
        this.updatedAt = board.getUpdatedAt();
    }
}
