// 수정: src/main/java/com/example/board/dto/BoardListResponse.java
package com.example.board.dto;

import com.example.board.domain.Board;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardListResponse {

    private final Long id;
    private final String title;
    private final String writerName;
    private final String writerUsername;
    private final int viewCount;
    private final int commentCount;
    private final LocalDateTime createdAt;

    public BoardListResponse(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.writerName = board.getWriterName();
        this.writerUsername = board.getMember() != null
                ? board.getMember().getUsername() : null;
        this.viewCount = board.getViewCount();
        this.commentCount = board.getCommentCount();
        this.createdAt = board.getCreatedAt();
    }
}
