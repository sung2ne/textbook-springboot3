package com.example.board.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "boards")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 댓글 관계 - 09장에서 추가
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt DESC")
    private List<Comment> comments = new ArrayList<>();

    // 첨부파일 관계 - 추가
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.viewCount = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public Board(String title, String content, Member member) {
        this.title = title;
        this.content = content;
        this.member = member;
    }

    // 게시글 수정 - 02장에서 작성
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // 조회수 증가 - 02장에서 작성
    public void increaseViewCount() {
        this.viewCount++;
    }

    // 작성자명 조회 - 02장에서 작성
    public String getWriterName() {
        return this.member != null ? this.member.getName() : "익명";
    }

    // 댓글 추가 - 09장에서 추가
    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    // 댓글 수 조회 - 09장에서 추가
    public int getCommentCount() {
        return this.comments.size();
    }

    // 첨부파일 추가 - 추가
    public void addAttachment(Attachment attachment) {
        this.attachments.add(attachment);
    }
}
