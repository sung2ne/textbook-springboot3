// 수정: src/main/java/com/example/board/repository/MemberRepository.java
package com.example.board.repository;

import com.example.board.domain.Member;
import com.example.board.domain.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 아이디로 회원 조회 (로그인용)
    Optional<Member> findByUsername(String username);

    // 아이디 중복 체크
    boolean existsByUsername(String username);

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Member> findByName(String name);

    // 키워드로 검색 (아이디, 이름, 이메일)
    @Query("SELECT m FROM Member m WHERE m.username LIKE %:keyword% OR m.name LIKE %:keyword% OR m.email LIKE %:keyword%")
    Page<Member> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 역할로 검색
    Page<Member> findByRole(Role role, Pageable pageable);

    // 키워드 + 역할로 검색
    @Query("SELECT m FROM Member m WHERE (m.username LIKE %:keyword% OR m.name LIKE %:keyword% OR m.email LIKE %:keyword%) AND m.role = :role")
    Page<Member> findByKeywordAndRole(@Param("keyword") String keyword, @Param("role") Role role, Pageable pageable);
}
