// 새 파일: src/main/java/com/example/board/domain/Attachment.java
package com.example.board.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "attachments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalFilename;  // 원본 파일명

    @Column(nullable = false)
    private String storedFilename;    // 저장 파일명

    @Column(nullable = false)
    private Long fileSize;            // 파일 크기 (bytes)

    private String contentType;       // MIME 타입

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Attachment(String originalFilename, String storedFilename,
                      Long fileSize, String contentType, Board board) {
        this.originalFilename = originalFilename;
        this.storedFilename = storedFilename;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.board = board;
    }

    // 이미지 여부 확인
    public boolean isImage() {
        return contentType != null && contentType.startsWith("image/");
    }
}
