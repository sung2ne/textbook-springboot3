package com.example.board.repository;

import com.example.board.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {

    // 제목 또는 내용으로 검색 (페이징)
    Page<Board> findByTitleContainingOrContentContaining(
            String title, String content, Pageable pageable);

    // 작성자명으로 검색
    Page<Board> findByWriterName(String writerName, Pageable pageable);

    // 제목으로 검색
    Page<Board> findByTitleContaining(String title, Pageable pageable);
}
