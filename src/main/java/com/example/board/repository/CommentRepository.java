package com.example.board.repository;

import com.example.board.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 게시글별 댓글 목록 (최신순)
    List<Comment> findByBoardIdOrderByCreatedAtDesc(Long boardId);

    // Fetch Join - 댓글과 작성자 함께 조회 (N+1 방지)
    @Query("SELECT c FROM Comment c JOIN FETCH c.member WHERE c.board.id = :boardId ORDER BY c.createdAt DESC")
    List<Comment> findByBoardIdWithMember(@Param("boardId") Long boardId);
}
