package com.example.board.repository;

import com.example.board.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    // 제목 또는 내용으로 검색 (페이징) - 02장에서 작성
    Page<Board> findByTitleContainingOrContentContaining(
            String title, String content, Pageable pageable);

    // 작성자명으로 검색 - 02장에서 작성
    Page<Board> findByWriterName(String writerName, Pageable pageable);

    // 제목으로 검색 - 02장에서 작성
    Page<Board> findByTitleContaining(String title, Pageable pageable);

    // 상세 조회 (Member Fetch Join) - 추가
    @Query("SELECT b FROM Board b LEFT JOIN FETCH b.member WHERE b.id = :id")
    Optional<Board> findByIdWithMember(@Param("id") Long id);
}
