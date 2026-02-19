// 수정: src/main/java/com/example/board/repository/BoardRepository.java
package com.example.board.repository;

import com.example.board.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    // 제목 또는 내용으로 검색 (페이징)
    Page<Board> findByTitleContainingOrContentContaining(
            String title, String content, Pageable pageable);

    // 작성자명으로 검색
    Page<Board> findByWriterName(String writerName, Pageable pageable);

    // 제목으로 검색
    Page<Board> findByTitleContaining(String title, Pageable pageable);

    // 상세 조회 (Member + 첨부파일 Fetch Join) - 10장 04절에서 수정
    @Query("SELECT b FROM Board b " +
           "LEFT JOIN FETCH b.member " +
           "LEFT JOIN FETCH b.attachments " +
           "WHERE b.id = :id")
    Optional<Board> findByIdWithMember(@Param("id") Long id);

    // 회원별 게시글 수 조회 - 12장/03
    long countByMemberId(Long memberId);

    // 회원별 게시글 목록 조회 - 13장/04
    List<Board> findByMemberId(Long memberId);

    // 회원별 게시글 목록 (페이징) - 추가
    Page<Board> findByMemberId(Long memberId, Pageable pageable);

    // 게시글 + 댓글 조회 (N+1 방지) - 추가
    @Query("SELECT DISTINCT b FROM Board b " +
           "LEFT JOIN FETCH b.member " +
           "LEFT JOIN FETCH b.comments c " +
           "LEFT JOIN FETCH c.member " +
           "WHERE b.id = :id")
    Optional<Board> findByIdWithMemberAndComments(@Param("id") Long id);
}
