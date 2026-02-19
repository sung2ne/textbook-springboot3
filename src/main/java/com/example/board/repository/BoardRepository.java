// 수정: src/main/java/com/example/board/repository/BoardRepository.java
package com.example.board.repository;

import com.example.board.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    // Fetch Join으로 Member와 함께 조회
    @Query("SELECT b FROM Board b JOIN FETCH b.member WHERE b.id = :id")
    Optional<Board> findByIdWithMember(Long id);

    // 목록 조회 (Fetch Join)
    @Query("SELECT b FROM Board b JOIN FETCH b.member ORDER BY b.id DESC")
    List<Board> findAllWithMember();
}
