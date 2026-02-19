package com.example.board.repository;

import com.example.board.domain.Board;
import com.example.board.dto.BoardSummary;
import com.example.board.dto.BoardSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    // ===== 02장: Fetch Join 메서드 =====
    @Query("SELECT b FROM Board b JOIN FETCH b.member WHERE b.id = :id")
    Optional<Board> findByIdWithMember(Long id);

    @Query("SELECT b FROM Board b JOIN FETCH b.member ORDER BY b.id DESC")
    List<Board> findAllWithMember();

    // ===== 01. 쿼리 메서드 =====
    List<Board> findByTitle(String title);
    List<Board> findByTitleContaining(String keyword);
    List<Board> findByMemberId(Long memberId);
    List<Board> findByViewCountBetween(int min, int max);
    List<Board> findByCreatedAtAfter(LocalDateTime date);
    List<Board> findByTitleContainingOrContentContaining(String title, String content);
    List<Board> findByMemberIdOrderByCreatedAtDesc(Long memberId);
    List<Board> findTop10ByOrderByViewCountDesc();

    // ===== 02. @Query - JPQL 직접 작성 =====
    @Query("SELECT b FROM Board b WHERE b.title LIKE %:keyword%")
    List<Board> searchByTitle(@Param("keyword") String keyword);

    @Query(value = "SELECT * FROM boards WHERE title LIKE %:keyword%", nativeQuery = true)
    List<Board> searchByTitleNative(@Param("keyword") String keyword);

    @Query("SELECT b FROM Board b WHERE b.title = :title AND b.member.id = :memberId")
    List<Board> findByTitleAndMemberId(@Param("title") String title,
                                        @Param("memberId") Long memberId);

    @Query("SELECT b FROM Board b WHERE b.member.name = :name")
    List<Board> findByMemberName(@Param("name") String name);

    @Query("SELECT b FROM Board b JOIN b.member m WHERE m.name = :name")
    List<Board> findByMemberNameWithJoin(@Param("name") String name);

    @Query("SELECT b FROM Board b JOIN FETCH b.member WHERE b.member.name = :name")
    List<Board> findByMemberNameWithFetch(@Param("name") String name);

    @Query("SELECT COUNT(b) FROM Board b WHERE b.member.id = :memberId")
    Long countBoardsByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT SUM(b.viewCount), AVG(b.viewCount) FROM Board b")
    Object[] getViewCountStats();

    @Query("SELECT MAX(b.viewCount) FROM Board b")
    Integer getMaxViewCount();

    @Query("SELECT new com.example.board.dto.BoardSummary(b.id, b.title, b.member.name) FROM Board b")
    List<BoardSummary> findBoardSummaries();

    @Query("SELECT b.id as id, b.title as title, m.name as memberName " +
           "FROM Board b JOIN b.member m")
    List<BoardSummaryProjection> findAllSummaries();

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Board b SET b.viewCount = 0 WHERE b.createdAt < :date")
    int resetOldViewCounts(@Param("date") LocalDateTime date);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Board b SET b.title = :newTitle WHERE b.id IN :ids")
    int updateTitles(@Param("newTitle") String newTitle, @Param("ids") List<Long> ids);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("DELETE FROM Board b WHERE b.member.id = :memberId")
    void deleteAllByMemberId(@Param("memberId") Long memberId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("DELETE FROM Board b WHERE b.createdAt < :date AND b.viewCount = 0")
    int deleteOldUnreadBoards(@Param("date") LocalDateTime date);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE Board b SET b.viewCount = b.viewCount + 1 WHERE b.id = :id")
    void incrementViewCount(@Param("id") Long id);

    // ===== 03. 페이징 메서드 =====
    Page<Board> findAll(Pageable pageable);

    // ===== 04. 검색 메서드 (이 페이지에서 추가) =====

    // 제목 검색 + 페이징
    Page<Board> findByTitleContaining(String keyword, Pageable pageable);

    // 내용 검색 + 페이징
    Page<Board> findByContentContaining(String content, Pageable pageable);

    // 제목 또는 내용 검색 (OR 조건) + 페이징
    Page<Board> findByTitleContainingOrContentContaining(
            String title, String content, Pageable pageable);

    // 작성자 이름으로 검색 (연관 엔티티 조건 - JPQL 필요)
    @Query("SELECT b FROM Board b JOIN b.member m WHERE m.name LIKE %:keyword%")
    Page<Board> findByMemberNameContaining(@Param("keyword") String keyword, Pageable pageable);

    // JPQL로 통합 검색 (null 허용)
    @Query("SELECT b FROM Board b " +
           "WHERE (:keyword IS NULL OR b.title LIKE %:keyword% OR b.content LIKE %:keyword%)")
    Page<Board> search(@Param("keyword") String keyword, Pageable pageable);
}
