// 수정: src/main/java/com/example/board/repository/CommentRepository.java
package com.example.board.repository;

import com.example.board.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 게시글의 댓글 + 회원 조회 (09장에서 작성)
    @Query("SELECT c FROM Comment c JOIN FETCH c.member WHERE c.board.id = :boardId ORDER BY c.createdAt DESC")
    List<Comment> findByBoardIdWithMember(@Param("boardId") Long boardId);

    // 게시글 삭제 시 댓글 삭제 (09장에서 작성)
    void deleteByBoardId(Long boardId);

    // 회원의 댓글 수 (마이페이지용) - 02장에서 추가
    long countByMemberId(Long memberId);

    // 회원의 댓글 목록 (회원 탈퇴 시 익명 처리용) - 02장에서 추가
    List<Comment> findByMemberId(Long memberId);

    // 회원의 댓글 목록 (마이페이지용, 페이징) - 02장에서 추가
    Page<Comment> findByMemberId(Long memberId, Pageable pageable);

    // 댓글 + 회원 조회 (권한 체크용, N+1 방지) - 02장에서 추가
    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.member WHERE c.id = :id")
    Optional<Comment> findByIdWithMember(@Param("id") Long id);

    // 회원의 댓글 + 게시글 정보 (마이페이지용, N+1 방지) - 추가
    @EntityGraph(attributePaths = {"board"})
    @Query("SELECT c FROM Comment c WHERE c.member.id = :memberId")
    Page<Comment> findByMemberIdWithBoard(@Param("memberId") Long memberId, Pageable pageable);
}
