package com.example.board.repository;

import com.example.board.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    // 제목 또는 내용 검색 (페이징)
    Page<Board> findByTitleContainingOrContentContaining(
            String title, String content, Pageable pageable);

    // Member와 첨부파일을 함께 조회 (Fetch Join) - 수정
    @Query("SELECT b FROM Board b " +
           "LEFT JOIN FETCH b.member " +
           "LEFT JOIN FETCH b.attachments " +
           "WHERE b.id = :id")
    Optional<Board> findByIdWithMember(@Param("id") Long id);
}
